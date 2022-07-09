package org.pjj.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.pjj.mail.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @author PengJiaJun
 * @Date 2022/3/21 14:43
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailApplicationTests {

    @Autowired
    MailService mailService;

    @Test
    public void sendSimpleMailTest() {
        //调用定义的发送文本邮件的方法
        mailService.sendSimpleMail("3119118981@qq.com", "这是第一封邮件(主题)", "这是邮件内容: 验证码为 888888");
    }

    @Test
    public void checkMa() {
        for (int j = 0; j < 1000; j++) {
            Random random = new Random();
            String result = "";
            for (int i=0;i<6;i++) {
                result += random.nextInt(10);
            }
            System.out.println(result);
        }
    }
}

