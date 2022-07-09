package org.pjj.eduservice.client;

import org.pjj.commonutils.R;
import org.pjj.eduservice.client.impl.VodClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 该 类 就是说 相当于service层吧, 由controller调用, 不过就是说 该类 是 调用其他 服务(此处是调用 service-vod) 中的controller, 而不是自己写实现类
 * @author PengJiaJun
 * @Date 2022/3/14 23:19
 */
//name表示 需要调用的 服务名 (在nacos中注册的服务) fallback 表示hystrix服务熔断, 当feign调用远程服务失败时, 会进行熔断调用 VodClientImpl.class 中的备用方法
@FeignClient(name = "service-vod", fallback = VodClientImpl.class)
@Component //必须要注入ioc容器, 因为后面调用 需要使用 @Autowired 注入
public interface VodClient {

    // 调用 service-vod 服务的 /eduvod/video/delete/{videoSourceId} 接口
    // 就是说 调用该 方法 就相当于调用 service-vod 中的 这个路径的方法
    // 注意: @PathVariable 注解 一定要指定参数名称 否则出错  如@PathVariable("videoScourseId")
    // 因为 @PathVariable("videoScourseId") 中的参数写错了 导致直接报 404
    @DeleteMapping("/eduvod/video/delete/{videoSourceId}")
    R deleteAliyunVideo(@PathVariable("videoSourceId") String videoSourceId);//传入需要删除视频的id (多个id 用, 分割 "id1, id2")

}
