package org.pjj.order.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 订单号工具类
 * @author PengJiaJun
 * @Date 2022/3/31 10:52
 */
public class OrderNoUtil {

    /**
     * 获取订单号
     *
     * 规则: 根据当前日期生成 14位 字符串, 然后生产3个随机整数(0-9) 与日期字符串拼接 生成订单号
     *
     * @return
     */
    public static String getOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate = sdf.format(new Date());
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }
        return newDate + result;
    }

}
