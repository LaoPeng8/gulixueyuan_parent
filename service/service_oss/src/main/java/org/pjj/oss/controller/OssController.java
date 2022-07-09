package org.pjj.oss.controller;

import org.pjj.commonutils.R;
import org.pjj.oss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author PengJiaJun
 * @Date 2022/2/26 18:27
 */
@RestController
@RequestMapping("/eduoss/fileoss")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class OssController {

    @Autowired
    private OssService ossService;

    //上传头像的方法
    @PostMapping
    public R uploadOssFile(MultipartFile file) {
        //获取上传文件 MultipartFile
        String url = ossService.uploadFileAvatar(file);//返回上传到oss的路径
        return R.ok().data("url", url);
    }

    // 删除 oss上的文件(包括: 视频, 头像, 封面)
    @DeleteMapping("/delFile/{filePath}")
    public R deleteFile(@PathVariable String filePath) {
        // 由于路径中的 / 与 @PostMapping中的 /{filePath} 冲突, 所以传递过来的 是 使用replace将 / 替换为了 -
// https:-- ... 2022-03-10-11-0803f55cc86b4013b6a1c15d5f9511a9cs1.jpg  => https:-- ... 2022/3/10/11/0803f55cc86b4013b6a1c15d5f9511a9cs1.jpg
        // 所以这里需要将 - 再转为 /
        filePath = filePath.replace('-', '/');
        boolean flag = ossService.deleteFile(filePath);
        if(flag) {
            return R.ok();
        }
        return R.error();
    }

}
