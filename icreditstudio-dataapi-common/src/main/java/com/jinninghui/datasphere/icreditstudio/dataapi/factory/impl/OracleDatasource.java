package com.jinninghui.datasphere.icreditstudio.dataapi.factory.impl;

import cn.hutool.core.util.StrUtil;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.DatasourceSync;

/**
 * @author xujie
 * @description mysql
 * @create 2021-08-25 15:25
 **/
public class OracleDatasource implements DatasourceSync {
    @Override
    public String getDatabaseName(String uri) {
        String s = StrUtil.subBefore(uri, "?", false);
        return StrUtil.subAfter(s, "/", true);
    }

    @Override
    public String getPageParamBySql(String querySql, Integer pageNum, Integer pageSize) {
        int index = (pageNum - 1) * pageSize;
        String addPageParam = new StringBuilder(querySql).append(" limit ").append(index).append(" , ").append(pageSize).toString();
        return addPageParam;
    }
}
