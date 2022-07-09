package org.pjj.educms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author PengJiaJun
 * @Date 2022/3/17 21:26
 */
@SpringBootApplication
@ComponentScan("org.pjj") //使该模块可以 扫描到其他模块的 spring注解
@MapperScan("org.pjj.educms.mapper")
@EnableDiscoveryClient // nacos注册
@EnableFeignClients // 使用feign需要使用该注解
public class CmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
    }
}
