package com.fosss.community;

import com.fosss.community.properties.MailProperty;
import com.fosss.community.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    void testMail() {
        //System.out.println(mailProperty.getHost());
        mailUtil.sendMail("1745179058@qq.com", "牛客论坛", "验证码：1111");
    }
}
