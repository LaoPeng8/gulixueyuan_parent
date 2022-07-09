package org.pjj.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 网关解决跨域 (之后就不用在Controller上加注解 @CrossOrigin 因为所有的请求都是先请求网关 由网关统一跳转下去)
 *
 * 如果网关加了跨域之后, Controller上的 跨域注解 @CrossOrigin 不去掉的话, 就会导致一个错误
 * has been blocked by CORS policy: The 'Access-Control-Allow-Origin' header contains multiple values '*, *', but only one is allowed.
 * 意思就是设置了2次跨域，但是只有一个是允许的，移除其中的任意一个就好了。如果服务器设置了允许跨域，使用Nginx代理里面就不需要了（或者就不用使用Nginx了）
 * 如果 网关上设置了允许跨域, Controller上就不需要@CrossOrigin允许跨域了
 *
 *
 * @author PengJiaJun
 * @Date 2022/4/5 18:06
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

}
