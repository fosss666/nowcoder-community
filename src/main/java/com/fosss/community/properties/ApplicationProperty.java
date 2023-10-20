package com.fosss.community.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: fosss
 * Date: 2023/8/27
 * Time: 20:05
 * Description: 配置文件中的配置
 */
@Component
@Getter
public class ApplicationProperty {
    //邮件相关
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String username;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${server.port}")
    private int port;
    @Value("${community.path.uploadPath}")
    private String uploadPath;

    //长图存储路径
    @Value("${wk.image.path}")
    private String wkImagePath;
    //wk 命令
    @Value("${wk.image.command}")
    private String wkImageCommand;

    //oss
    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.file.keyid}")
    private String keyid;
    @Value("${aliyun.oss.file.keysecret}")
    private String keysecret;
    @Value("${aliyun.oss.file.bucketname}")
    private String bucketname;

    //caffeine
    @Value("caffeine.posts.max-size")
    private Integer maxSize;
    @Value("caffeine.posts.expire-seconds")
    private Integer expireSeconds;
}
