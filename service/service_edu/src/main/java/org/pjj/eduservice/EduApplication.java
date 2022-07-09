package org.pjj.eduservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author PengJiaJun
 * @Date 2022/2/13 0:31
 */
@SpringBootApplication
@EnableDiscoveryClient //nacos注册
@EnableFeignClients // 使用feign需要该注解
@ComponentScan("org.pjj") //扫描各种spring注解如: @Controller //为什么以前不用? 因为 @SpringBootApplication 会扫描主启动类所在包即以下
                          //而现在 引入的service_base模块中的配置类SwaggerConfig.java 在 org.pjj.servicebase包中 与主启动类的 org.pjj.eduservice
                          //不在同一个包下, 所以需要 额外配置 @ComponentScan来扫描 org.pjj 这样所有的包就都可以扫描到了.
public class EduApplication {
    public static void main(String[] args) {
        SpringApplication.run(EduApplication.class, args);
    }
}
