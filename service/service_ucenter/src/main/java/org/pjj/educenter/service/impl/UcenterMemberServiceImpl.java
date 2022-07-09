package org.pjj.educenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.pjj.commonutils.JwtUtils;
import org.pjj.commonutils.utils.MD5;
import org.pjj.educenter.entity.UcenterMember;
import org.pjj.educenter.entity.vo.LoginVo;
import org.pjj.educenter.entity.vo.RegisterVo;
import org.pjj.educenter.mapper.UcenterMemberMapper;
import org.pjj.educenter.service.UcenterMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.pjj.servicebase.exceptionhandler.exception.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author pjj
 * @since 2022-03-21
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UcenterMemberMapper ucenterMemberMapper;

    // 登录的方法
    @Override
    public String login(LoginVo member) {
        // 获取登录邮箱 与 密码
        String mail = member.getMail();
        String password = member.getPassword();

        // 判断 是否为空
        if(StringUtils.isEmpty(mail) || StringUtils.isEmpty(password)){
            throw new GuliException(20001, "邮箱或密码为空");
        }

        // 判断邮箱是否正确
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mail", mail);
        UcenterMember user = baseMapper.selectOne(wrapper);
        if(null == user) {//没有这个邮箱
            throw new GuliException(20001, "该用户不存在");
        }

        // 判断密码 (将用户输入的密码加密后 与 数据库中存的加密后的密码 比较)
        if(!MD5.md5Encrypt(password).equals(user.getPassword())) {
            throw new GuliException(20001, "密码错误");
        }

        // 判断用户是否被禁用
        if(user.getIsDisabled()) {
            throw new GuliException(20001, "该用户已被封禁, 请正确使用本网站!!!");
        }

        // 登录成功 根据用户id  与 用户名 生成token
        String jwtToken = JwtUtils.getJwtToken(user.getId(), user.getNickname());

        return jwtToken;
    }

    // 注册的方法
    @Override
    public void register(RegisterVo registerVo) {
        String code = registerVo.getCode();
        String mail = registerVo.getMail();
        String nickname = registerVo.getNickname();
        String password = registerVo.getPassword();

        // 判断 是否为空
        if(StringUtils.isEmpty(code) || StringUtils.isEmpty(mail) || StringUtils.isEmpty(nickname) || StringUtils.isEmpty(password)) {
            throw new GuliException(20001, "用户名, 密码, 邮箱, 验证码 一个都不能为空!!!");
        }

//        TODO 判断密码格式 如长度, 必须包括大小字母数字

        //判断验证码
        //获取redis中的验证码, 发送邮件时将验证码根据 key = 邮箱号, value = 验证码 存入了redis, 所以此处去除与用户从邮件看到的验证码 然后输入的验证码是否一致
        String redisCode = redisTemplate.opsForValue().get(registerVo.getMail());
        if (StringUtils.isEmpty(redisCode)) {
            // 如果redis中取出的验证码为空, 要么就是 获取验证码时的邮箱与此时注册的邮箱不一致, 要么就是 验证码过期了 所以取出的为空字符串
            throw new GuliException(20001, "验证码以过期, 请重新发送");
        }
        if (!redisCode.equals(code)) {//用户输入验证码 与 redis中的验证码比较
            throw new GuliException(20001, "验证码错误, 请重新输入");
        }

        // 判断 该邮箱 是否已经注册
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mail", mail);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new GuliException(20001, "该邮箱已注册, 请换一个试试叭");
        }

        //将数据添加至数据库中 完成注册
        UcenterMember ucenterMember = new UcenterMember();
        ucenterMember.setNickname(nickname);
        ucenterMember.setMail(mail);
        ucenterMember.setPassword(MD5.md5Encrypt(password));//将密码加密后存入数据库
        ucenterMember.setIsDisabled(false);//默认没有被封号
        ucenterMember.setAvatar("https://guli-edu-2022-2-26.oss-cn-beijing.aliyuncs.com/YF.jpg");//默认头像

        baseMapper.insert(ucenterMember);

    }

    /**
     * 根据 openId 查询用户
     * @param openId
     * @return
     */
    @Override
    public UcenterMember getOpenIdMember(String openId) {

        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openId);

        UcenterMember ucenterMember = baseMapper.selectOne(wrapper);

        return ucenterMember;
    }

    //查询某一天注册人数 day = "2022-4-3"
    @Override
    public Integer registerCount(String day) {
        return ucenterMemberMapper.registerCountDay(day);
    }
}
