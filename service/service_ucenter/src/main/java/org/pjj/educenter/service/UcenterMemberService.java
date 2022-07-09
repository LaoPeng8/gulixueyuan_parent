package org.pjj.educenter.service;

import org.pjj.educenter.entity.UcenterMember;
import com.baomidou.mybatisplus.extension.service.IService;
import org.pjj.educenter.entity.vo.LoginVo;
import org.pjj.educenter.entity.vo.RegisterVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author pjj
 * @since 2022-03-21
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    // 登录
    String login(LoginVo member);

    // 注册
    void register(RegisterVo registerVo);

    // 根据 openId 查询用户
    UcenterMember getOpenIdMember(String openId);

    //查询某一天注册人数 day = "2022-4-3"
    Integer registerCount(String day);
}
