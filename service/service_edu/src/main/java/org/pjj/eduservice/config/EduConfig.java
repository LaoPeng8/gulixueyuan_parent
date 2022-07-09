package org.pjj.eduservice.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * @author PengJiaJun
 * @Date 2022/2/13 0:35
 */
@Configuration
@MapperScan("org.pjj.eduservice.mapper")
public class EduConfig {

    /**
     * mybatis-plus 逻辑删除 插件
     * @return
     */
    @Bean
    public ISqlInjector iSqlInjector() {
        return new LogicSqlInjector();
    }

    /**
     * mybatis-plus 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * restTemplate 后端便携http请求工具
     * 它提供了常见的REST请求方案的模版，例如 GET 请求、POST 请求、PUT 请求、DELETE 请求
     *
     * 直接new RestTemplate(); 然后调用接口返回的结果有乱码;
     * 经百度发现, RestTemplate()的构造方法中 new StringHttpMessageConverter() 有个这个, 这个中 使用的默认编码为 iso-8859-1   DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
     *
     * 使用下面 这种方式返回的restTemplate就是使用的 utf-8 的编码方式
     * @return
     */
    @Bean
    public static RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> list = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> httpMessageConverter : list) {
            if(httpMessageConverter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) httpMessageConverter).setDefaultCharset(Charset.forName("utf-8"));
                break;
            }
        }
        return restTemplate;
    }
}
