package org.pjj.mail.controller;

import org.pjj.commonutils.R;
import org.pjj.mail.service.MailService;
import org.pjj.mail.utils.CheckNumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 发送邮件(注册)
 * @author PengJiaJun
 * @Date 2022/3/21 15:45
 */
@RestController
@RequestMapping("/edumail/mail")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class MailController {

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 发送 注册验证码 邮件
    @GetMapping("/{to}")
    public R sendSimpleMail(@PathVariable String to) {
        if(StringUtils.isEmpty(to)) {
            return R.error().message("收件人不能为空");
        }

        Pattern p = Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");//正则表达式
        Matcher m = p.matcher(to);//需要匹配的字符串
		boolean flag = m.matches();// matches() 尝试将整个字符序列与该正则匹配
        if (!flag) {
            return R.error().message("请输入正确的邮箱地址");
        }

        String checkNumber = CheckNumberUtils.getCheckNumber();//随机数(6位)
        String subject = "XiaoPeng的小破站";
        String content = "【XiaoPeng】您好，您正在注册 XiaoPeng的小破站，验证码：" + checkNumber + "，有效期为10分钟";

        mailService.sendSimpleMail(to, subject, content);//发送邮件
        redisTemplate.opsForValue().set(to, checkNumber, 10, TimeUnit.MINUTES);//将验证码存入redis key:邮箱, value:验证码 过期时间 10 分钟

        return R.ok();

    }

}
