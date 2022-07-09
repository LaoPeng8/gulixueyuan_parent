package org.pjj.oss.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 根据前缀 ConfigurationProperties(prefix = "aliyun.oss.file") 从 application.properties 中获取属性值
 * 例如:
 * private String endpoint; 就可以通过application.properties 中的 aliyun.oss.file.endpoint=oss-cn-beijing.aliyuncs.com 获取值
 * @author PengJiaJun
 * @Date 2022/2/26 17:59
 */
@Component
@ConfigurationProperties(prefix = "aliyun.oss.file")
public class ConstantPropertiesUtils implements InitializingBean {

    private String endpoint;

    private String keyid;

    private String keysecret;

    private String bucketname;

//    定义公开的静态常量
    public static String END_POINT;
    public static String ACCESS_KEY_ID;
    public static String ACCESS_KEY_SECRET;
    public static String BUCKET_NAME;

    /**
     * bean 生命周期 init
     * 会在 bean 被初始化时调用
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = this.endpoint;
        ACCESS_KEY_ID = this.keyid;
        ACCESS_KEY_SECRET = this.keysecret;
        BUCKET_NAME = this.bucketname;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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

    public String getBucketname() {
        return bucketname;
    }

    public void setBucketname(String bucketname) {
        this.bucketname = bucketname;
    }


}
