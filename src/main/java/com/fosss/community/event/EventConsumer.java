package com.fosss.community.event;

import com.alibaba.fastjson2.JSON;
import com.fosss.community.constant.EventConstant;
import com.fosss.community.constant.ExceptionConstant;
import com.fosss.community.entity.Event;
import com.fosss.community.entity.Message;
import com.fosss.community.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
}
