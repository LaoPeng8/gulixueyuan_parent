package org.pjj.mail.service;

/**
 * @author PengJiaJun
 * @Date 2022/3/21 14:37
 */
public interface MailService {

    /**
     * 发送简单邮件
     * @param to 收件人
     * @param subject 邮件主题
     * @param content 邮件内如
     */
    void sendSimpleMail(String to, String subject, String content);

}
