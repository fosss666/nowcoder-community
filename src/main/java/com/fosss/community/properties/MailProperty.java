package com.fosss.community.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: fosss
 * Date: 2023/8/27
 * Time: 20:05
 * Description:
 */
@Component
@Data
public class MailProperty {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String username;

}
