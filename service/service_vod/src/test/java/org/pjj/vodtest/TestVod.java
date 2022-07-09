package org.pjj.vodtest;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.*;
import org.junit.Test;
import org.pjj.vod.utils.AliyunVideoProperties;
import org.springframework.boot.SpringBootConfiguration;

import java.util.List;

/**
 * @author PengJiaJun
 * @Date 2022/3/11 19:46
 */
public class TestVod {

    private static final String ACCESS_KEY_ID = "****************";
    private static final String ACCESS_KEY_SECRET = "**************";

    public static void main(String[] args) {
//        getPlayUrl("5ad20bdc190e410f8ae61e821691e380");
//        System.out.println("\n=========================\n");
//        getPlayAuth("5ad20bdc190e410f8ae61e821691e380");

//        deleteVideo("7b0f1742a056441e8fb26aa4ae475a7f");
//        uploadVideo("测试本地上传文件", "D:/cs2.mp4");


        getPlayAuth("8754290858a848498b33c07fd534f29d");
    }

    /**
     * 本地文件上传接口
     * @param title 上传之后的文件名
     * @param fileName 本地文件路径和名称 如: E:/xxx/xx/潜江话之最.mp4
     * @return response.getVideoId();//返回的 上传视频之后 该视频的id值, 后续数据库就存储视频的id值, 之后通过id值 请求视频地址, 请求视频播放凭证, 请求删除视频
     */
    public static String uploadVideo(String title, String fileName) {

        //创建获取上传视频的 request
        UploadVideoRequest request = new UploadVideoRequest(ACCESS_KEY_ID, ACCESS_KEY_SECRET, title, fileName);//
        request.setPartSize(2 * 1024 * 1024L);//可指定分片上传时每个分片的大小，默认为2M字节
        request.setTaskNum(1);//可指定分片上传时的并发线程数，默认为1，(注：该配置会占用服务器CPU资源，需根据服务器情况指定)


        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadVideoResponse response = uploader.uploadVideo(request);//真正的上传方法(返回的 response 中有请求返回的各种数据)
        System.out.println("RequestId=" + response.getRequestId());  //请求视频点播服务的请求ID
        if (response.isSuccess()) {
            System.out.println("VideoId=" + response.getVideoId());
            return response.getVideoId();//返回的 上传视频之后 该视频的id值, 后续数据库就存储视频的id值, 之后通过id值 请求视频地址, 请求视频播放凭证, 请求删除视频
        } else {
            /* 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因 */
            // 意思就是 response.isSuccess() 不管是true 或 false 都会返回 videoId 如果返回了 也就是 response.getVideoId() 有值那么就是可以用(成功了)
            // 如果 response.getVideoId() 返回的视频id为 空, 那么就是上传失败了, 通过返回错误码分析具体错误原因
            System.out.println("VideoId=" + response.getVideoId());
            System.out.println("ErrorCode=" + response.getCode());
            System.out.println("ErrorMessage=" + response.getMessage());
            return response.getVideoId();
        }

    }

    /**
     * 根据视频id 删除视频
     * @param id 例如删除单个视频: request.setVideoIds("VideoId1"); ,  删除多个视频: request.setVideoIds("VideoId1,VideoId2");
     * @return
     */
    public static void deleteVideo(String id) {
        //获取初始化对象
        DefaultAcsClient client = InitObject.initVodClient(ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        //创建获取删除视频的 request 和 response
        DeleteVideoRequest request = new DeleteVideoRequest();
        request.setVideoIds(id);//传入需要删除视频的id (多个id 用, 分割 "id1, id2")

        DeleteVideoResponse response = null;
        try {
            response = client.getAcsResponse(request);//(返回的 response 中有请求返回的各种数据)

        } catch (ClientException e) {
            System.out.println("ErrorMessage = " + e.getLocalizedMessage());
        }

    }

    /**
     * 根据视频id 获取视频播放凭证
     * @param id
     * @return
     */
    public static String getPlayAuth(String id) {
        //获取初始化对象
        DefaultAcsClient client = InitObject.initVodClient(ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        //创建获取视频凭证的 request 和 response
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        //向request对象里面设置视频id
        request.setVideoId(id);//5ad20bdc190e410f8ae61e821691e380

        GetVideoPlayAuthResponse response = null;
        try {

            response = client.getAcsResponse(request);//(返回的 response 中有请求返回的各种数据)

            //播放凭证
            System.out.println("PlayAuth = " + response.getPlayAuth());
            //VideoMeta信息
            System.out.println("VideoMeta.Title = " + response.getVideoMeta().getTitle());

            return response.getPlayAuth();//播放凭证
        } catch (ClientException e) {
            System.out.println("ErrorMessage = " + e.getLocalizedMessage());
        }

        System.out.println("RequestId = " + response.getRequestId());

        return null;
    }

    /**
     * 根据视频id 获取视频播放地址
     * @param id
     * @return
     */
    public static String getPlayUrl(String id) {
        // 1. 根据视频id获取视频播放地址
        //创建初始化对象
        DefaultAcsClient client = InitObject.initVodClient(ACCESS_KEY_ID, ACCESS_KEY_SECRET);

        //创建获取视频地址的 request 和 response
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        //向request对象里面设置视频id
        request.setVideoId(id);//5ad20bdc190e410f8ae61e821691e380

        GetPlayInfoResponse response = null;
        try {
            //调用初始化对象里面的方法, 传递request, 获取数据
            response = client.getAcsResponse(request);

            List<GetPlayInfoResponse.PlayInfo> playInfoList = response.getPlayInfoList();
            //播放地址
            for(GetPlayInfoResponse.PlayInfo playInfo: playInfoList) {
                System.out.println("PlayInfo.PlayURL = " + playInfo.getPlayURL());//根据视频Id 返回的 播放地址
                return playInfo.getPlayURL();
            }

            //Base信息
            System.out.println("VideoBase.Title = " + response.getVideoBase().getTitle()); //文件名

        } catch (ClientException e) {
            System.out.println("ErrorMessage = " + e.getLocalizedMessage());
        }

        System.out.println("RequestId = " + response.getRequestId());//本次请求id (每次都不一样)

        return null;
    }

}
