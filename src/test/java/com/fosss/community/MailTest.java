package com.fosss.community;

import com.fosss.community.properties.MailProperty;
import com.fosss.community.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

/**
 * @author: fosss
 * Date: 2023/8/27
 * Time: 20:06
 * Description:
 */
@SpringBootTest
public class MailTest {

    @Resource
    private MailProperty mailProperty;
    @Resource
    private MailUtil mailUtil;
    @Resource
    private TemplateEngine templateEngine;

    @Test
    void testTextMail() {
        //System.out.println(mailProperty.getHost());
        mailUtil.sendMail("1745179058@qq.com", "牛客论坛", "验证码：1111");
    }

    @Test
    void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "詹尼");
        String process = templateEngine.process("/mail/demo", context);
        System.out.println(process);

        mailUtil.sendMail("1745179058@qq.com", "牛客论坛", process);
    }
}
