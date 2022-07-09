package org.pjj.order.client;

import org.pjj.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author PengJiaJun
 * @Date 2022/3/30 23:44
 */
@FeignClient(name = "service-ucenter")
@Component
public interface UcenterMemberClient {

    //根据id获取用户信息
    @GetMapping("/educenter/member/getMemberInfo/byId/{id}")
    R getMemberInfoById(@PathVariable("id") String id);

}
