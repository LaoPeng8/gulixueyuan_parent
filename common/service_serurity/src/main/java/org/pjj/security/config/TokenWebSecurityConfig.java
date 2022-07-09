package org.pjj.security.config;

import org.pjj.security.filter.TokenAuthFilter;
import org.pjj.security.filter.TokenLoginFilter;
import org.pjj.security.security.DefaultPasswordEncoder;
import org.pjj.security.security.TokenLogoutHandler;
import org.pjj.security.security.TokenManager;
import org.pjj.security.security.UnAuthEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author PengJiaJun
 * @Date 2022/4/17 22:24
 */
@Configuration
public class TokenWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private TokenManager tokenManager;
    private RedisTemplate<String, Object> redisTemplate;
    private DefaultPasswordEncoder defaultPasswordEncoder;//自定义的密码处理类
    private UserDetailsService userDetailsService;

    /**
     * 关于 在构造方法上使用 @Autowired
     * 1.spring创建bean的顺序先会推断构造方法然后再做依赖注入，如果在没有@Autowired注解的构造里调用的通过@Autowired注入变量的方法，会报NPE，因为此时还没有依赖注入。
     * 2.如果类中只有一个有参构造，spring会从容器中通过类型找到该参数类型（如果通过类型找到多个则再通过名字匹配），注入到参数中，@Autowired只是程序员告诉Spring使用该构造方法作为创建bean的构造方法。
     * @param userDetailsService
     * @param defaultPasswordEncoder
     * @param tokenManager
     * @param redisTemplate
     */
    @Autowired
    public TokenWebSecurityConfig(UserDetailsService userDetailsService, DefaultPasswordEncoder defaultPasswordEncoder, TokenManager tokenManager, RedisTemplate<String, Object> redisTemplate) {
        this.userDetailsService = userDetailsService;
        this.defaultPasswordEncoder = defaultPasswordEncoder;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置 退出的地址 和 token, redis操作地址
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                //设置 未登录统一处理类
                .authenticationEntryPoint(new UnAuthEntryPoint())
                //设置关闭 csrf防护
                .and().csrf().disable()
                //设置所有请求都需要身份认证
                .authorizeRequests()
                .anyRequest().authenticated()
                //设置退出成功后访问该地址
                .and().logout().logoutSuccessUrl("/admin/acl/index/logout")
                //设置退出登录自定义类, 该类中有 退出登录的具体方法
                .addLogoutHandler(new TokenLogoutHandler(tokenManager, redisTemplate))
                //添加一个过滤器 认证过滤器, 该类中有 获取前端登录表单提交过来数据的方法, 登录成功后执行的方法 与 登录失败后执行的方法
                .and().addFilter(new TokenLoginFilter(authenticationManager(), tokenManager, redisTemplate))
                .addFilter(new TokenAuthFilter(authenticationManager(), tokenManager, redisTemplate)).httpBasic();
    }


    /**
     * 调用 userDetailService 进行 用户认证(登录) 和 设置密码处理的类(对密码使用 啥方式进行加密)
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(defaultPasswordEncoder);
    }

    /**
     * 配置哪些请求不拦截
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/**",
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**"
        );
    }
}
