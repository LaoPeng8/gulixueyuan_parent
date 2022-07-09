package org.pjj.security.security;

import org.pjj.commonutils.R;
import org.pjj.commonutils.utils.ResponseUtil;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 未授权统一处理
 *
 * @author PengJiaJun
 * @Date 2022/4/16 18:19
 */
public class UnAuthEntryPoint implements AuthenticationEntryPoint {

    /**
     * 当用户请求了一个受保护的资源, 但是用户没有通过认证, 那么抛出异常, AuthenticationEntryPoint.commence(..)就会被调用
     *
     * 实现 AuthenticationEntryPoint 中的 commence 方法, 就是说 当用户请求了一个受保护的资源, 但是用户没有通过认证 会执行我们自定义的 操作
     * 返回 json 数据, 告知 前端调用者 没有权限...
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param e
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ResponseUtil.out(httpServletResponse, R.error().message("没有权限"));//返回json数据
    }

}
