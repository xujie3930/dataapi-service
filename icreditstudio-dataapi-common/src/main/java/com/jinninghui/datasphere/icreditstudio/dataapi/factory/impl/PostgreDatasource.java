package com.jinninghui.datasphere.icreditstudio.dataapi.factory.impl;

import cn.hutool.core.util.StrUtil;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.DatasourceSync;

/**
 * @author xujie
 * @description postgresql
 * @create 2022-04-18 15:25
 **/
public class PostgreDatasource implements DatasourceSync {

    /**
     * 取得数据库名称
     * @param uri
     * @return
     */
    @Override
    public String getDatabaseName(String uri) {
        String s = StrUtil.subBefore(uri, "?", false);
        return StrUtil.subAfter(s, "/", true);
    }

    @Override
    public String getPageParamBySql(String querySql, Integer pageNum, Integer pageSize) {
        int index = (pageNum - 1) * pageSize;
        String addPageParam = new StringBuilder(querySql).append(" limit ").append(pageSize).append(" offset ").append(index).toString();
        return addPageParam;
    }
}
