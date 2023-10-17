package com.fosss.community.event;

import com.alibaba.fastjson2.JSON;
import com.fosss.community.constant.EventConstant;
import com.fosss.community.constant.ExceptionConstant;
import com.fosss.community.entity.DiscussPost;
import com.fosss.community.entity.Event;
import com.fosss.community.entity.Message;
import com.fosss.community.properties.ApplicationProperty;
import com.fosss.community.service.DiscussPostService;
import com.fosss.community.service.ElasticsearchService;
import com.fosss.community.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.fosss.community.constant.ExceptionConstant.EVENT_SHARE_ERROR;

/**
 * @author: fosss
 * Date: 2023/10/3
 * Time: 23:07
 * Description:消费者
 */
@Slf4j
@Component
public class EventConsumer {

    @Resource
    private MessageService messageService;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private ElasticsearchService elasticsearchService;
    @Resource
    private ApplicationProperty property;

    /**
     * 消费事件
     */
    @KafkaListener(topics = {EventConstant.EVENT_TOPIC_COMMENT, EventConstant.EVENT_TOPIC_LIKE, EventConstant.EVENT_TOPIC_FOLLOW})
    public void handleEvent(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error(ExceptionConstant.EVENT_CONTENT_NULL);
            return;
        }
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error(ExceptionConstant.EVENT_FORMAT_ERROR);
            return;
        }

        //封装消息
        Message message = new Message();
        message.setFromId(EventConstant.SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        Map<String, Object> content = new HashMap<>();
        content.put(EventConstant.EVENT_CONTENT_USERID, event.getUserId());
        content.put(EventConstant.EVENT_CONTENT_ENTITY_ID, event.getEntityId());
        content.put(EventConstant.EVENT_CONTENT_ENTITY_TYPE, event.getEntityType());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSON.toJSONString(content));
        messageService.addMessage(message);
    }

    /**
     * 消费帖子发布事件
     */
    @KafkaListener(topics = EventConstant.EVENT_TOPIC_PUBLISH)
    public void handlePublicEvent(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error(ExceptionConstant.EVENT_CONTENT_NULL);
            return;
        }
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error(ExceptionConstant.EVENT_FORMAT_ERROR);
            return;
        }
        //查询帖子
        DiscussPost discussPost = discussPostService.selectById(event.getEntityId());
        //保存到es
        elasticsearchService.saveDiscussPost(discussPost);
    }

    /**
     * 消费帖子删除事件
     */
    @KafkaListener(topics = EventConstant.EVENT_TOPIC_PUBLISH)
    public void handleDeleteEvent(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error(ExceptionConstant.EVENT_CONTENT_NULL);
            return;
        }
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error(ExceptionConstant.EVENT_FORMAT_ERROR);
            return;
        }

        //从es中删除
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

    /**
     * 生成长图
     */
    @KafkaListener(topics = EventConstant.EVENT_TOPIC_SHARE)
    public void handleShareEvent(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error(ExceptionConstant.EVENT_CONTENT_NULL);
            return;
        }
        //解析出事件
        Event event = JSON.parseObject(record.value().toString(), Event.class);
        //拼接生成图片的命令
        String htmlUrl = (String) event.getData().get("htmlUrl");
        String fileName = (String) event.getData().get("fileName");
        String suffix = (String) event.getData().get("suffix");

        String cmd = property.getWkImageCommand() + " --quality 75 "
                + htmlUrl + " " + property.getWkImagePath() + "/" + fileName + suffix;
        //执行命令
        try {
            Runtime.getRuntime().exec(cmd);
            log.info("生成长图成功: " + cmd);
        } catch (IOException e) {
            log.error(EVENT_SHARE_ERROR + ":" + e);
        }
    }
}
