package org.pjj.statistics.client;

import org.pjj.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author PengJiaJun
 * @Date 2022/4/3 1:43
 */
@FeignClient(name = "service-ucenter")
@Component
public interface UcenterMember {

    //查询某一天注册人数 day = "2022-4-3"
    @GetMapping("/educenter/member/registerCount/{day}")
    R registerCount(@PathVariable("day") String day);//返回对象为 r.getData().get("registerCount");

}
