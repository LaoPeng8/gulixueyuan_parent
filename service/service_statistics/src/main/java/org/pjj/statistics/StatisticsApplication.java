package org.pjj.statistics;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author PengJiaJun
 * @Date 2022/4/3 1:19
 */
@SpringBootApplication
@EnableFeignClients //使用feign
@EnableDiscoveryClient //nacos注册
@ComponentScan("org.pjj") //使之可以扫描到其他公共模块中的Spring组件
@MapperScan("org.pjj.statistics.mapper") //mybatis-plus扫描mapper
@EnableScheduling //开启定时任务的注解
public class StatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatisticsApplication.class, args);
    }
}
