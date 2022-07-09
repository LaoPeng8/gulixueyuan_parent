package org.pjj.vod.service.impl;


import com.aliyun.oss.ClientException;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import org.pjj.vod.service.VodService;
import org.pjj.vod.utils.AliyunVideoProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author PengJiaJun
 * @Date 2022/3/12 23:32
 */
@Service
public class VodServiceImpl implements VodService {

//    这样貌似赋不了值...
//    private String regionId = AliyunVideoProperties.REGION_ID;
//    private String accessKeyId = AliyunVideoProperties.ACCESS_KEY_ID;
//    private String accessKeySecret = AliyunVideoProperties.ACCESS_KEY_SECRET;
//    private String testTest = "testTest";

    // 初始化
    public static DefaultAcsClient initVodClient() throws ClientException {
        // 点播服务接入区域(阿里云可以看 存储管理中看自己是 那个区域) (华东2（上海）是cn-shanghai, 华北2（北京）cn-beijing, ...)
        String regionId = AliyunVideoProperties.REGION_ID;
        String accessKeyId = AliyunVideoProperties.ACCESS_KEY_ID;
        String accessKeySecret = AliyunVideoProperties.ACCESS_KEY_SECRET;
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

    //上传文件到阿里云
    @Override
    public String uploadVideoAliyun(MultipartFile file) {
        try {

            InputStream inputStream = file.getInputStream();//将用户上传的文件 转为输入流
            String fileName = file.getOriginalFilename();//获取用户上传的视频 的 文件名
            String title = fileName.substring(0, fileName.lastIndexOf("."));//上传到阿里云后的视频名称 (截取原始文件名, 不要后缀.mp4)
            UploadStreamRequest request = new UploadStreamRequest(AliyunVideoProperties.ACCESS_KEY_ID, AliyunVideoProperties.ACCESS_KEY_SECRET, title, fileName, inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);

            String videoId = null;
            if(response.isSuccess()) {
                videoId = response.getVideoId();
            }else {
                /* 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因 */
//                System.out.println("ErrorCode=" + response.getCode());
//                System.out.println("ErrorMessage=" + response.getMessage());

                videoId = response.getVideoId();
            }

            return videoId;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据视频id 删除视频
     * @param id 例如删除单个视频: request.setVideoIds("VideoId1"); ,  删除多个视频: request.setVideoIds("VideoId1,VideoId2");
     * @return
     */
    public boolean deleteVideo(String id) {
        //获取初始化对象
        DefaultAcsClient client = initVodClient();

        //创建获取删除视频的 request 和 response
        DeleteVideoRequest request = new DeleteVideoRequest();
        request.setVideoIds(id);//传入需要删除视频的id (多个id 用, 分割 "id1, id2")

        DeleteVideoResponse response = null;
        try {
            response = client.getAcsResponse(request);//(返回的 response 中有请求返回的各种数据)

        } catch (com.aliyuncs.exceptions.ClientException e) {
            System.out.println("ErrorMessage = " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    @Override
    public String getPlayAuth(String id) {
        //获取初始化对象
        DefaultAcsClient client = initVodClient();

        //创建获取视频凭证的 request 和 response
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        //向request对象里面设置视频id
        request.setVideoId(id);//例如: 5ad20bdc190e410f8ae61e821691e380

        GetVideoPlayAuthResponse response = null;
        try {

            response = client.getAcsResponse(request);//(返回的 response 中有请求返回的各种数据)
            return response.getPlayAuth();
            //VideoMeta信息
//            System.out.println("VideoMeta.Title = " + response.getVideoMeta().getTitle());
        } catch (com.aliyuncs.exceptions.ClientException e) {
            System.out.println("ErrorMessage = " + e.getLocalizedMessage());
        }

        System.out.println("RequestId = " + response.getRequestId());
        return null;
    }

}
