package org.pjj.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.pjj.commonutils.R;
import org.pjj.commonutils.utils.ResponseUtil;
import org.pjj.security.entity.SecurityUser;
import org.pjj.security.entity.User;
import org.pjj.security.security.TokenManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * 认证过滤器
 *
 * 获取 从表单提交的信息 (UsernamePasswordAuthenticationFilter也是可以获取的, 默认是接收 username password)
 * 这里是就是重写了父类的方法 attemptAuthentication 来自定义接收 从表单提交的数据
 *
 * 然后重写了一个 登录成功后执行的方法
 * 与
 * 重写了一个 登录失败后执行的方法
 *
 * @author PengJiaJun
 * @Date 2022/4/16 18:37
 */
public class TokenLoginFilter extends UsernamePasswordAuthenticationFilter {

    private TokenManager tokenManager;
    private RedisTemplate<String, Object> redisTemplate;
    private AuthenticationManager authenticationManager;

    public TokenLoginFilter() {
    }
    public TokenLoginFilter(AuthenticationManager authenticationManager, TokenManager tokenManager, RedisTemplate<String, Object> redisTemplate) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
        this.setPostOnly(false);
        //设置登录地址
        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/admin/acl/login", "POST"));
    }

    /**
     * 获取表单提交的用户名, 密码 (具体认证的方法 可能在父类中)
     *
     * @param request
     * @param response
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            //获取表单提交数据 封装为 user
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            // 返回的类型为 Authentication, 所以需要将 user 封装为需要的类型返回
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList<>()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 认证成功 执行该方法
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //认证成功后, 得到认证成功之后用户信息
        SecurityUser user = (SecurityUser) authResult.getPrincipal();

        //根据用户名生成 token
        String token = tokenManager.createToken(user.getCurrentUserInfo().getUsername());

        //把用户名称 和 用户权限列表放到 redis (key="username", value="用户权限列表")
        redisTemplate.opsForValue().set(user.getCurrentUserInfo().getUsername(), user.getPermissionValueList());

        //返回 登录成功的 token
        ResponseUtil.out(response, R.ok().data("token", token)); //返回 登陆成功的json字符串
    }

    /**
     * 认证失败 执行该方法
     *
     * @param request
     * @param response
     * @param failed
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        ResponseUtil.out(response, R.error().message("login error"));//返回 登录失败的json字符串
    }
}
