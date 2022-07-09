package org.pjj.mail.service.impl;

import org.pjj.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author PengJiaJun
 * @Date 2022/3/21 14:36
 */
@Service
public class MailServiceImpl implements MailService {

    @Autowired
    JavaMailSender javaMailSender;

    // 邮件发送者
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 普通邮件发送
     */
    public void sendSimpleMail(String to, String subject, String content) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        message.setSubject(subject);
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        message.setFrom(from);
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，类似 message.setTo("10*****16@qq.com","12****32*qq.com");
        message.setTo(to);
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文
        message.setText(content);
// 设置邮件抄送人，可以有多个抄送人
// message.setCc("12****32*qq.com");
// 设置隐秘抄送人，可以有多个
// message.setBcc("7******9@qq.com");
        // 发送邮件
        javaMailSender.send(message);
    }

}
