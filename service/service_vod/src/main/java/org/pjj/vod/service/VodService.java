package org.pjj.vod.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author PengJiaJun
 * @Date 2022/3/12 23:32
 */
public interface VodService {

    // 上传文件到阿里云 返回视频id
    String uploadVideoAliyun(MultipartFile file);

    // 根据视频id 删除阿里云视频
    boolean deleteVideo(String id);

    // 根据视频id 获取视频凭证
    String getPlayAuth(String id);

}
