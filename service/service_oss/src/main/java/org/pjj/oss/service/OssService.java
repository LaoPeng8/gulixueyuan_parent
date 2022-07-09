package org.pjj.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author PengJiaJun
 * @Date 2022/2/26 18:27
 */
public interface OssService {
    /**
     * 长传头像到 oss
     * @param file
     * @return 返回图片路径
     */
    String uploadFileAvatar(MultipartFile file);

    /**
     * 删除 oss上的文件(包括: 视频, 头像, 封面)
     * @param filePath
     * @return
     */
    boolean deleteFile(String filePath);
}
