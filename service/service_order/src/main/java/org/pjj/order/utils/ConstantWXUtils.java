package org.pjj.order.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author PengJiaJun
 * @Date 2022/4/2 00:25
 */
@Component
public class ConstantWXUtils implements InitializingBean {

    @Value("${wx.pay.appid}")
    private String appid;//关联的公众号id

    @Value("${wx.pay.partner}")
    private String partner;//商户号

    @Value("${wx.pay.partnerkey}")
    private String partnerkey;//商户key

    @Value("${wx.pay.notifyurl}")
    private String notifyurl;//回调地址

    public static String WX_APP_ID;
    public static String WX_PARTNER;
    public static String WX_PARTNERKEY;
    public static String WX_NOTIFYURL;

    /**
     * 会在bean初始化时执行
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        WX_APP_ID = appid;
        WX_PARTNER = partner;
        WX_PARTNERKEY = partnerkey;
        WX_NOTIFYURL = notifyurl;
    }

}
