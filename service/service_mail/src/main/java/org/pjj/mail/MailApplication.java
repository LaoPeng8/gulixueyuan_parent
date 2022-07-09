package org.pjj.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author PengJiaJun
 * @Date 2022/3/21 14:11
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //不自动配置数据库
@ComponentScan("org.pjj")
@EnableDiscoveryClient //nocos注册
public class MailApplication {
    public static void main(String[] args) {
        SpringApplication.run(MailApplication.class, args);
    }
}
