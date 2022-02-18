package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.util.UUID;

/**
 * 描述 ：uuid生成工具类
 */
public class UUIDUtils {

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
