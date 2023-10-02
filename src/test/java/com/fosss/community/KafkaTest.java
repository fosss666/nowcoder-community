package com.fosss.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/10/2
 * Time: 14:18
 * Description:
 */
@SpringBootTest
public class KafkaTest {
    @Resource
    private KafkaProducer kafkaProducer;

    @Test
    public void test() {
        kafkaProducer.send("test", "hello world");
        kafkaProducer.send("test", "你好！");
        kafkaProducer.send("test", "啦啦啦啦啦非基督教覅及司法及·！");

        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

@Component
class KafkaProducer {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}

@Component
class KafkaConsumer {

    @KafkaListener(topics = {"test"})
    public void handlerMessage(ConsumerRecord record) {
        System.out.println("监听到消息：" + record.topic() + "->" + record.value());
    }
}