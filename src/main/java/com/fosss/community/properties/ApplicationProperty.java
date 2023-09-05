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

}
