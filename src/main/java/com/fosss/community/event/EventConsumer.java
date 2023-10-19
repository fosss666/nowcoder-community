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
import com.fosss.community.service.OssService;
import com.fosss.community.utils.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.util.StringMap;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

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
    @Resource
    private OssService ossService;
    @Resource
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

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
        // 启用定时器,监视该图片,一旦生成了,则上传至七牛云.
        UploadTask task = new UploadTask(fileName, suffix);
        Future future = threadPoolTaskScheduler.scheduleAtFixedRate(task, 500);
        task.setFuture(future);
    }

    class UploadTask implements Runnable {

        // 文件名称
        private String fileName;
        // 文件后缀
        private String suffix;
        // 启动任务的返回值
        private Future future;
        // 开始时间
        private long startTime;
        // 上传次数
        private int uploadTimes;

        public UploadTask(String fileName, String suffix) {
            this.fileName = fileName;
            this.suffix = suffix;
            this.startTime = System.currentTimeMillis();
        }

        public void setFuture(Future future) {
            this.future = future;
        }

        @Override
        public void run() {
// 生成失败
            if (System.currentTimeMillis() - startTime > 30000) {
                log.error("执行时间过长,终止任务:" + fileName);
                future.cancel(true);
                return;
            }
            // 上传失败
            if (uploadTimes >= 3) {
                log.error("上传次数过多,终止任务:" + fileName);
                future.cancel(true);
                return;
            }
            String path = property.getWkImagePath() + "/" + fileName + suffix;
            File file = new File(path);
            if (file.exists()) {
                log.info(String.format("开始第%d次上传[%s].", ++uploadTimes, fileName));
                try {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    MockMultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                            ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
                    String imageUrl = ossService.uploadFile(multipartFile);
                    System.err.println("@@@@@@长图访问路径：" + imageUrl);
                    future.cancel(true);//停止定时任务
                } catch (IOException e) {
                    log.error("长图上传oss失败");
                }
            } else {
                log.info("等待图片生成[" + fileName + "].");
            }
        }
    }
}
