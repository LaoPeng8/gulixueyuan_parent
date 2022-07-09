package org.pjj.vod.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 根据前缀 ConfigurationProperties(prefix = "aliyun.video.file") 从 application.properties 中获取属性值
 * 例如:
 * private String regionid; 就可以通过application.properties 中的 aliyun.video.file.regionid=cn-shanghai 获取值
 *
 * @author PengJiaJun
 * @Date 2022/3/12 23:47
 */
@Component
@ConfigurationProperties(prefix = "aliyun.video.file")
public class AliyunVideoProperties implements InitializingBean {

    private String regionid;

    private String keyid;

    private String keysecret;

    //    定义公开的静态常量
    public static String REGION_ID;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;

    /**
     * bean 生命周期 init
     * 会在 bean 被初始化时调用
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        REGION_ID = this.regionid;
        ACCESS_KEY_ID = this.keyid;
        ACCESS_KEY_SECRET = this.keysecret;
    }

    public String getRegionid() {
        return regionid;
    }

    public void setRegionid(String regionid) {
        this.regionid = regionid;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getKeysecret() {
        return keysecret;
    }

    public void setKeysecret(String keysecret) {
        this.keysecret = keysecret;
    }
}
