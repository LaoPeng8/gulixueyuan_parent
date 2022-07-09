package org.pjj.educms.client;

import org.pjj.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author PengJiaJun
 * @Date 2022/3/17 23:12
 */
@FeignClient(name = "service-oss")
@Component
public interface OssClient {

//    feign 好像 不太支持传文件, 报各种错误, 好像需要导入其他依赖 和 配置
//    @PostMapping("/eduoss/fileoss")
//    R uploadOssFile(@RequestParam("file") MultipartFile file);

    @DeleteMapping("/eduoss/fileoss/delFile/{filePath}")
    R deleteFile(@PathVariable("filePath") String filePath);

}
