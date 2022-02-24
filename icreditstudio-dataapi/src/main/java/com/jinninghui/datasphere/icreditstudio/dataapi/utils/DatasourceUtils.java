package com.jinninghui.datasphere.icreditstudio.dataapi.utils;

import cn.hutool.core.util.StrUtil;

/**
 * @author xujie
 * @description mysql
 * @create 2021-08-25 15:25
 **/
public class DatasourceUtils {

    /**
     * 取得数据库名称
     *
     * @param uri
     * @return
     */
    public static String getDatabaseName(String uri) {
        String s = StrUtil.subBefore(uri, "?", false);
        return StrUtil.subAfter(s, "/", true);
    }

}
