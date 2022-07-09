package org.pjj.security.security;

import org.pjj.commonutils.utils.MD5;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自定义密码处理类, md5 加密密码.
 *
 * 虽然 SpringSecurity 提供的 加密的方式 如: BCryptPasswordEncoder.java (这些类都是实现了PasswordEncoder接口的) (规定只有实现了该接口才是SpringSecurity处理密码的类)
 * 虽然 SpringSecurity 提供了很多 PasswordEncoder 的实现类, 但是由于项目是使用的 md5 加密, 所以我们需要自定义密码处理的类
 *
 * @author PengJiaJun
 * @Date 2022/4/16 16:49
 */
@Component
public class DefaultPasswordEncoder implements PasswordEncoder {

    //两个构造方法很迷糊
    public DefaultPasswordEncoder() {
        this(-1);
    }
    public DefaultPasswordEncoder(int strength) {
    }

    /**
     * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or
     * greater hash combined with an 8-byte or greater randomly generated salt.
     *
     * @param rawPassword
     */
    @Override //加密密码, 输入原始密码, 返回 我们使用自定义的加密方式加密后的密码
    public String encode(CharSequence rawPassword) {
        return MD5.md5Encrypt(rawPassword.toString());
    }

    /**
     * Verify the encoded password obtained from storage matches the submitted raw
     * password after it too is encoded. Returns true if the passwords match, false if
     * they do not. The stored password itself is never decoded.
     *
     * @param rawPassword     the raw password to encode and match 原始密码进行编码和匹配
     * @param encodedPassword the encoded password from storage to compare with 要与之比较的已编码密码
     * @return true if the raw password, after encoding, matches the encoded password from
     * storage
     */
    @Override //进行密码的比对
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5.md5Encrypt(rawPassword.toString()));
    }

}
