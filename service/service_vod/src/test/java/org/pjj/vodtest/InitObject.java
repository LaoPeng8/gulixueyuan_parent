package org.pjj.vodtest;

import com.aliyun.oss.ClientException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;

/**
 * @author PengJiaJun
 * @Date 2022/3/11 18:49
 */
public class InitObject {

    // 初始化
    public static DefaultAcsClient initVodClient(String accessKeyId, String accessKeySecret) throws ClientException {
        // 点播服务接入区域(阿里云可以看 存储管理中看自己是 那个区域) (华东2（上海）是cn-shanghai, 华北2（北京）cn-beijing, ...)
        String regionId = "cn-shanghai";
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        return client;
    }

}
