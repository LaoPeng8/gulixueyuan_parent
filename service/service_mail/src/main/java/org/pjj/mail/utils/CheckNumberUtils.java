package org.pjj.mail.utils;

import java.util.Random;

/**
 * @author PengJiaJun
 * @Date 2022/3/21 19:30
 */
public class CheckNumberUtils {

    // 返回 6 位随机数
    public static String getCheckNumber() {
        Random random = new Random();
        String result = "";
        for (int i=0;i<6;i++) {
            result += random.nextInt(10);
        }
        return result;
    }

}
