package org.pjj.eduservice.client;

import org.pjj.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 本服务 service-edu 通过 该接口 调用 service-oss 中提供的接口
 * @author PengJiaJun
 * @Date 2022/3/15 13:00
 */
@FeignClient(name = "service-oss") //表示 需要调用的 服务名 (在nacos中注册的服务)
@Component //必须要注入ioc容器, 因为后面调用 需要使用 @Autowired 注入
public interface OssClient {

    /**
     * 删除 oss上的文件(图片)(包括: 头像, 封面)
     * PathVariable("filePath") 参数一定要写, 否则404
     * @param filePath 参数不能直接是 http://ip:port/2022/3/10/11/0803f55cc86b4013b6a1c15d5f9511a9cs1.jpg  / 会与 方法路径/{filePath} 产生冲突
     *                 约定是 传入需要删除的图片地址时 将 / 转为 - 进行参数传递
     * @return
     */
    @DeleteMapping("/eduoss/fileoss/delFile/{filePath}") //路由一定是 全路径
    R deleteFile(@PathVariable("filePath") String filePath);

}
