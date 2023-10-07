package com.fosss.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {
    //TODO 如果启动es发生错误的话在来考虑启动这段代码
    /*@PostConstruct
    public void init() {
        // 解决netty启动冲突问题
        // Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }*/

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}
