package org.pjj.eduservice.client.impl;

import org.pjj.commonutils.R;
import org.pjj.eduservice.client.VodClient;
import org.springframework.stereotype.Component;

/**
 * hystrix 熔断机制
 *
 * VodClient.java接口中 定义了 需要远程调用的方法, 就是调用VodClient.java中的方法 然后 feign 就会根据你调用的方法调用 其他服务中对应的方法
 * 但是有时请求时间会很久, hystrix 默认请求超时时间为 1s 我们通过配置文件修改为了 6s
 * 就是说 如果我们请求 VodClient.java 中的方法 6s 没有返回, 相当于超时了, 为了避免出现问题 (如 对应服务宕机了, 你一直请求也不会返回的, 导致本服务内存爆满 也宕机了)
 * 所以为了避免 就会有本文件 VodClient.java 的实现类, 当hystrix发现 调用 VodClient.java中的方法 超时了(6s) 就会熔断该方法
 * 继而调用 本类 中 对应的方法(相当于备用方法) 从而起到保护的作用
 *
 * @author PengJiaJun
 * @Date 2022/3/15 22:14
 */
@Component
public class VodClientImpl implements VodClient {

    // 只有调用 VodClient 中的 deleteAliyunVideo 方法出错了,
    // 才会执行 该类中的 deleteAliyunVideo
    @Override
    public R deleteAliyunVideo(String videoSourceId) {
        return R.error().message("time out");
    }
}
