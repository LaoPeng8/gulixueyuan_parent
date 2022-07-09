package org.pjj.educms.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author PengJiaJun
 * @Date 2022/3/18 16:39
 */
@Configuration
public class CmsConfig {

    /**
     * mybatis-plus 逻辑删除 插件
     * @return
     */
    @Bean
    public ISqlInjector iSqlInjector() {
        return new LogicSqlInjector();
    }

}
