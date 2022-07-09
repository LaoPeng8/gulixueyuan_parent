package org.pjj.educenter.controller;


import org.pjj.commonutils.JwtUtils;
import org.pjj.commonutils.R;
import org.pjj.educenter.entity.UcenterMember;
import org.pjj.educenter.entity.vo.LoginVo;
import org.pjj.educenter.entity.vo.RegisterVo;
import org.pjj.educenter.entity.vo.ResultVo;
import org.pjj.educenter.service.UcenterMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author pjj
 * @since 2022-03-21
 */
@RestController
@RequestMapping("/educenter/member")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService ucenterMemberService;

    //登录
    @PostMapping("/login")
    public R loginUser(@RequestBody LoginVo member) {

        // 登录成功 返回 token 值 (使用Jwt生成)
        String token = ucenterMemberService.login(member);

        return R.ok().data("token", token);
    }

    //注册
    @PostMapping("/register")
    public R registerUser(@RequestBody RegisterVo registerVo) {

        ucenterMemberService.register(registerVo);

        return R.ok();
    }

    //根据token获取用户信息
    @GetMapping("/getMemberInfo")
    public R getMemberInfo(HttpServletRequest request) {

        String memberId = JwtUtils.getMemberIdByJwtToken(request);//从请求头中获取token, 根据token获取用户id
        if (StringUtils.isEmpty(memberId)) {
            return R.error();
        }

        UcenterMember member = ucenterMemberService.getById(memberId);//根据id查询出用户信息

        //member是直接从数据库查出来的, 有很多敏感信息不能给前台看的, 所以封装为vo类给前台展示一部分数据
        ResultVo resultVo = new ResultVo();
        BeanUtils.copyProperties(member, resultVo);//将member的属性值赋值给resultVo

        return R.ok().data("userInfo", resultVo);
    }

    //根据id获取用户信息
    @GetMapping("/getMemberInfo/byId/{id}")
    public R getMemberInfoById(@PathVariable String id) {
        if(StringUtils.isEmpty(id)) {
            return R.error();
        }

        UcenterMember member = ucenterMemberService.getById(id);//根据id查询出用户信息

        //member是直接从数据库查出来的, 有很多敏感信息不能给前台看的, 所以封装为vo类给前台展示一部分数据
        ResultVo resultVo = new ResultVo();
        BeanUtils.copyProperties(member, resultVo);//将member的属性值赋值给resultVo

        return R.ok().data("userInfo", resultVo);
    }

    //查询某一天注册人数 day = "2022-4-3"
    @GetMapping("/registerCount/{day}")
    public R registerCount(@PathVariable String day) {

        Integer count = ucenterMemberService.registerCount(day);

        return R.ok().data("registerCount", count);
    }

}

