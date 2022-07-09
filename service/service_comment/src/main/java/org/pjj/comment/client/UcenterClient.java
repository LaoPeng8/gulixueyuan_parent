package org.pjj.comment.client;

import org.pjj.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.servlet.http.HttpServletRequest;

/**
 * @author PengJiaJun
 * @Date 2022/3/29 1:11
 */
@FeignClient(name = "service-ucenter")
@Component
public interface UcenterClient {

    // (暂时不需要了, 也不删除, 后面需要用到该接口的时候再用把)
    //通过feign远程调用 service-ucenter 中的接口: /educenter/member/getMemberInfo
    @GetMapping("/educenter/member/getMemberInfo")
    R getMemberInfo(@RequestHeader("token") String token);//设置请求头 token

}
