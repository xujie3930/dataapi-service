package com.jinninghui.datasphere.icreditstudio.dataapi.factory.impl;

import cn.hutool.core.util.StrUtil;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.DatasourceSync;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (querySql.contains("limit")){
            querySql = querySql.substring(0, querySql.indexOf("limit"));
        }
        int index = (pageNum - 1) * pageSize;
        String addPageParam = new StringBuilder(querySql).append(" limit ").append(pageSize).append(" offset ").append(index).toString();
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
            m.appendReplacement(sb, "'" + value + "'");
        }
        m.appendTail(sb);
        String tempSql = sb.toString();
        //对string做选填处理
        if (tempSql.contains("where")){
            tempSql = tempSql.replaceAll("where", "where 1=1 and ");
        }
        if (!CollectionUtils.isEmpty(noRequiredSet)){
            for (String field : noRequiredSet) {
                tempSql = tempSql.replaceAll("and " + field + " = " + "'null'", "");
            }
        }


        //pg数据库需要对所有字段加上""
        int begin = tempSql.indexOf("select");
        int last = tempSql.indexOf("from");
        String substring = tempSql.substring(begin + "select".length(), last).replaceAll(" ", "");
        String[] split = substring.split(",");
        StringBuilder builder = new StringBuilder("select ");
        for (String key : split) {
            builder.append("\"" + key + "\"" + ",");
        }
        String s = builder.toString();
        String sql= s.substring(0, s.length() -1) + tempSql.substring(tempSql.lastIndexOf("from"));

        Set set = kvs.entrySet();

        Iterator i = set.iterator();

        String resp = sql.substring(sql.indexOf("and"));
        String param = sql.substring(0, sql.indexOf("and"));

        while(i.hasNext()){

            Map.Entry<String, String> entry=(Map.Entry<String, String>)i.next();

            String key = entry.getKey();

            resp = resp.replaceAll(key, "\"" + key + "\"");

        }
        return param + resp;
    }

}
