package org.pjj.vod.controller;

import org.pjj.commonutils.R;
import org.pjj.vod.client.EduClient;
import org.pjj.vod.service.VodService;
import org.pjj.vod.utils.AliyunVideoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @author PengJiaJun
 * @Date 2022/3/12 23:34
 */
@RestController
@RequestMapping("/eduvod/video")
//@CrossOrigin 网关设置了跨域, 这里不需要了, 如还是使用则会报错
public class VodController {

    @Autowired
    private VodService vodService;

    @Autowired
    private EduClient eduClient;

    // 上传视频到阿里云 (返回视频id)
    @PostMapping("/uploadaliyunVideo")
    public R uploadAliyunVideo(MultipartFile file) {

        String videoId = vodService.uploadVideoAliyun(file);

        return R.ok().data("videoId", videoId);
    }

    /**
     * 根据视频id 删除视频
     * @param videoSourceId 传入需要删除视频的id (多个id 用, 分割 "id1, id2")
     * @return
     */
    @DeleteMapping("/delete/{videoSourceId}")
    public R deleteAliyunVideo(@PathVariable String videoSourceId) {

        boolean flag = vodService.deleteVideo(videoSourceId);

        if(flag) {
            return R.ok();
        }

        return R.error();
    }

    /**
     * 根据视频id 获取视频播放凭证
     * @param id
     * @return
     */
    @GetMapping("getPlayAuth/{id}")
    public R getPlayAuth(@PathVariable String id, HttpServletRequest request) {
        String playAuth = vodService.getPlayAuth(id);

        // 视频浏览数 + 1, 规则: 同一ip下 访问某个视频 该视频浏览数 + 1, 但是10分钟内只能 +1 一次
        eduClient.updateVideoPlayCount(id);

        return R.ok().data("playAuth", playAuth);
    }

}
