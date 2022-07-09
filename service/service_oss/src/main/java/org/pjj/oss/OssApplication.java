package org.pjj.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 阿里云 oss 服务 启动类
 *
 * 启动报错: Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
 * 很显然意思是 没有找到 DataSource url 的配置, 但是我们并没有引入 数据库的依赖,
 * 点击idea右侧菜单的Maven发现 该项目是引入了 MyBatis-plus 那么很显然是 MyBatis-plus 中有DataSource然后又没有设置 url 所以报错
 * 实际上是因为 因为父项目service引入了 mybatis-plus-boot-starter, mysql-connector-java 所以 导致DataSource报错
 * 主要是 mybatis-plus, mysql驱动中应该是不包含DataSource的
 *
 * 解决:
 * 可以就设置一个 datasource 的 name, url, password, driver (治标不治本, 不是正常解法, 这样不好)
 *
 * 可以在 父项目service中不引入 mysql-plus + mysql驱动, 父项目service下具体需要这两个依赖的再具体引入, 这样本项目中就不会有 DataSource 就不会报错
 *
 * 可以 本项目启动类 注解中加 exclude = DataSourceAutoConfiguration.class
 * 也就是  @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
 * 表示 SpringBoot 排除 自动配置 数据库(DataSourceAutoConfiguration) 也就是不自动配置数据库, 这样就算引入了依赖, 不用就不会报错了.
 *
 * @author PengJiaJun
 * @Date 2022/2/26 12:02
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(value = "org.pjj")
@EnableDiscoveryClient // nacos注册
public class OssApplication {
    public static void main(String[] args) {
        SpringApplication.run(OssApplication.class, args);
    }
}
