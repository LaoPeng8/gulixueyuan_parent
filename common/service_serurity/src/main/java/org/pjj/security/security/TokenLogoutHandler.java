package org.pjj.security.security;

import org.pjj.commonutils.R;
import org.pjj.commonutils.utils.ResponseUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义退出登录类
 *
 * @author PengJiaJun
 * @Date 2022/4/16 17:53
 */
public class TokenLogoutHandler implements LogoutHandler {

    private TokenManager tokenManager;
    private RedisTemplate<String, Object> redisTemplate; //其实也可以使用 @Autowired 注入

    public TokenLogoutHandler() {}
    public TokenLogoutHandler(TokenManager tokenManager, RedisTemplate<String, Object> redisTemplate) {
        //通过构造器 传入 tokenManager 操作token, 传入redisTemplate 操作redis
        this.tokenManager = tokenManager;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 注销的方法
     *
     *
     * @param request
     * @param response
     * @param authentication
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 1. 从 header 里面 获取token
        // 2. token 不为空, 移除token, 从redis中删除 token
        String token = request.getHeader("token");
        if(!StringUtils.isEmpty(token)) {
            //移除 token
            tokenManager.removeToken(token);

            //从 token 中获取用户名, 从redis中删除 token (key=用户名, value=token)
            String username = tokenManager.getUserInfoFromToken(token);
            redisTemplate.delete(username);
        }

        ResponseUtil.out(response, R.ok());//返回json格式信息
    }
}
