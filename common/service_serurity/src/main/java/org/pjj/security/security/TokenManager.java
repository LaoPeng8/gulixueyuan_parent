package org.pjj.security.security;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 * Token管理
 *
 * 我好奇的是 common_utils模块中有一个 JwtUtils 也是生成token 和 验证token的, 为什么还需要再写一个 本类
 *
 * @author PengJiaJun
 * @Date 2022/4/16 17:20
 */
@Component
public class TokenManager {

    //token有效时长 一天
    private long tokenTTL = 24 * 60 * 60 * 1000;// 一天

    // jwt 编码密钥
    private String tokenSignKey = "I LOVE YOU"; //一般为随机字符串

    /**
     * 根据 username 生成 token 并返回
     * @param username
     * @return
     */
    public String createToken(String username) {
        String token = Jwts.builder()
                .setSubject(username) //token中需要存储的数据, 一般为 id, 用户名
                .setExpiration(new Date(System.currentTimeMillis() + tokenTTL))//过期时间 = 当前时间 + 有效时长
                .signWith(SignatureAlgorithm.HS512, tokenSignKey).compressWith(CompressionCodecs.GZIP).compact();//设置加密方式, 密钥, ...

        return token;
    }

    /**
     * 根据 token 字符串 得到用户信息(得到存储在token中的信息)
     * @param token
     * @return
     */
    public String getUserInfoFromToken(String token) {
        String userInfo = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token).getBody().getSubject();
        return userInfo;
    }

    /**
     * 删除 token (实际不需要删除, 前端将保存在本地的 token 删除后, 再次请求后端就相当于是退出登录了)
     *
     * @param token
     */
    public void removeToken(String token) {
    }

}
