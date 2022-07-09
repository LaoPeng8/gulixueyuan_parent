package org.pjj.eduservice.controller;

import io.swagger.annotations.Api;
import org.pjj.commonutils.R;
import org.springframework.web.bind.annotation.*;

/**
 * @author PengJiaJun
 * @Date 2022/2/22 13:36
 */
@Api(value = "讲师管理", tags = {"讲师管理"}) //swagger 定义该Controller的中文解释
@RestController
@RequestMapping("eduservice/user")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class EduLoginController {

    /**
     * 登录接口
     * @return
     */
    @PostMapping("/login")
    public R login() {
        //先模拟登录 返回token 后面再使用JWT生成token
        return R.ok().data("token", "ADMIN");
    }

    /**
     * 返回登录用户信息
     * @return
     */
    @GetMapping("/info")
    public R info() {
        //模拟从token中获取登录用户信息, 并返回
        return R.ok()
                .data("roles", "[admin]")
                .data("name", "admin")
                .data("avatar", "http://110.42.250.118/img/PiKaQiu.gif");
    }

}
