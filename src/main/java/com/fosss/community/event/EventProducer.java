package com.fosss.community.event;

import com.alibaba.fastjson.JSON;
import com.fosss.community.entity.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/10/3
 * Time: 23:04
 * Description: 生产者
 */
@Component
public class EventProducer {

    @Resource
    private KafkaTemplate kafkaTemplate;

    /**
     * 发送事件
     *
     * @param event 内容
     */
    public void fireEvent(Event event) {
        kafkaTemplate.send(event.getTopic(), JSON.toJSONString(event));
    }
}
