package org.pjj.educenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author PengJiaJun
 * @Date 2022/3/21 22:02
 */
@SpringBootApplication
@ComponentScan("org.pjj")
@MapperScan("org.pjj.educenter.mapper")
@EnableDiscoveryClient //nacos注册
@CrossOrigin
public class UCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UCenterApplication.class, args);
    }
}
