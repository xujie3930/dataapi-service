package com.jinninghui.datasphere.icreditstudio.dataapi.factory.impl;

import cn.hutool.core.util.StrUtil;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.DatasourceSync;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xujie
 * @description mysql
 * @create 2021-08-25 15:25
 **/
public class MysqlDatasource implements DatasourceSync {

    @Override
    public String geturi(String uri) {
        return uri;
    }

    /**
     * 取得数据库名称
     *
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
        if (querySql.contains("limit")){
            querySql = querySql.substring(0, querySql.indexOf("limit"));
        }
        int index = (pageNum - 1) * pageSize;
        String addPageParam = new StringBuilder(querySql).append(" limit ").append(index).append(" , ").append(Math.min((MAX_IMUM - index), pageSize)).toString();
        return addPageParam;
    }

    @Override
    public String parseSql(String content, Map<String, String> kvs) {
        //必填参数
        Set<String> noRequiredSet = new HashSet<>();
        Pattern p = Pattern.compile("\\$\\{.*?\\}");
        Matcher m = p.matcher(content);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            String key = group.replace("${", "").replace("}",  "").replaceAll(" ", "");
            String value = kvs.get(key);
            if (value == null){
                noRequiredSet.add(key);
            }
            if (isContainWildcard(content, group)){
                m.appendReplacement(sb, value);
            }else{
                m.appendReplacement(sb, "'" + value + "'");
            }
        }
        m.appendTail(sb);
        String tempSql = sb.toString();
        //对string做选填处理
        if (!CollectionUtils.isEmpty(noRequiredSet)){
            for (String field : noRequiredSet) {
                tempSql = tempSql.replaceAll("and " + field + " = " + "'null'", "");
                tempSql = tempSql.replaceAll(field + " = " + "'null'", "");
            }
        }
        if (tempSql.contains("where")){
            tempSql = tempSql.replaceAll("where", "where 1=1 and ");
        }
        return tempSql;
    }

    @Override
    public String getFileld(String field) {
        return field;
    }

}
