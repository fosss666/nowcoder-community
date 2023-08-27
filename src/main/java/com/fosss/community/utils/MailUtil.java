package com.fosss.community.utils;

import com.fosss.community.constant.ExceptionConstant;
import com.fosss.community.properties.MailProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author: fosss
 * Date: 2023/8/27
 * Time: 20:09
 * Description: 发送邮件
 */
@Component
@Slf4j
public class MailUtil {

    @Resource
    private MailProperty mailProperty;
    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 发送邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param context 内容
     */
    public void sendMail(String to, String subject, String context) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(mailProperty.getUsername());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(context, true);// true：内容支持html格式
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(ExceptionConstant.Mail_ERROR + "：" + e.getMessage());
        }
    }

}

















