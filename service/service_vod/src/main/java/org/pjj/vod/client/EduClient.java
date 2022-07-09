package org.pjj.vod.client;

import org.pjj.commonutils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @author PengJiaJun
 * @Date 2022/4/3 14:53
 */
@FeignClient(name = "service-edu")
@Component
public interface EduClient {

    /**
     * 传入 视频id 找到该视频 并且播放次数 +1
     * @param videoSourceId 视频id(不是主键id)
     * @return
     */
    @PutMapping("/eduservice/video/playCount/{videoSourceId}")
    R updateVideoPlayCount(@PathVariable("videoSourceId") String videoSourceId);

}
