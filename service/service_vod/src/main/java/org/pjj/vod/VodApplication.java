package org.pjj.vod;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author PengJiaJun
 * @Date 2022/3/12 19:56
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(value = "org.pjj")
@EnableDiscoveryClient //nacos注册
@EnableFeignClients
public class VodApplication {
    public static void main(String[] args) {
        SpringApplication.run(VodApplication.class, args);
    }
}
