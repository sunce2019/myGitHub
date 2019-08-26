package com.pay.util;

import java.util.UUID;

/**
 * Created by frank on 2017/10/9.
 */
public class UIDUtil {

    public static String getUUID() {
        // 使用JDK自带的UUID生成器
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
