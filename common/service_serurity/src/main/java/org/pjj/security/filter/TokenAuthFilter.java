package org.pjj.security.filter;

import org.pjj.security.security.TokenManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 权限过滤器 (授权过滤器)
 * 就是说 请求过来后 根据请求头中携带的token, 获取出登录用户, 然后根据用户名 从redis中获取该用户的权限信息(用户登录时从数据库获取到权限信息, 并放入redis),
 * 然后放到SpringSecurity权限上下文中, 相当于授权
 *
 * @author PengJiaJun
 * @Date 2022/4/16 23:56
 */
public class TokenAuthFilter extends BasicAuthenticationFilter {

    private TokenManager tokenManager;
    private RedisTemplate<String, Object> redisTemplate;

    public TokenAuthFilter(AuthenticationManager authenticationManager, TokenManager tokenManager, RedisTemplate<String, Object> redisTemplate) {
        super(authenticationManager);
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //获取当前认证成功用户的权限信息
        UsernamePasswordAuthenticationToken authRequest = getAuthentication(request);

        //判断如果有权限信息, 放到权限上下文中
        if(authRequest != null) {
            SecurityContextHolder.getContext().setAuthentication(authRequest);
        }

        chain.doFilter(request, response);//放行

    }

    //获取当前认证成功用户的权限信息
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        //从 header 中获取 token
        String token = request.getHeader("token");
        if(token != null) {
            //如果 token 不为空, 则 根据token 获取存放在token中的username
            String username = tokenManager.getUserInfoFromToken(token);

            //从 redis 中获取对应权限列表 (之前登录的表单提交后, 认证成功后, 会根据username生成token, 同时key=username将该用户的权限列表存入redis)
            List<String> permissionValueList = (List<String>) redisTemplate.opsForValue().get(username);

            //封装为 UsernamePasswordAuthenticationToken 返回, 需要的是 Collection<GrantedAuthority>类型
            //所以需要将 List 转为 Collection
            Collection<GrantedAuthority> authority = new ArrayList<>();
            for(String permissionValue : permissionValueList) {
                authority.add(new SimpleGrantedAuthority(permissionValue));
            }

            return new UsernamePasswordAuthenticationToken(username, token, authority);
        }

        return null;
    }



}
