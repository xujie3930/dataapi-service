package com.jinninghui.datasphere.icreditstudio.dataapi.factory;

import cn.hutool.core.util.StrUtil;
import com.jinninghui.datasphere.icreditstudio.framework.utils.sm4.SM4Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public interface DatasourceSync {

    Logger logger = LoggerFactory.getLogger(DatasourceSync.class);
    String SEPARATOR = "|";
    String URL_SEPARATOR = "?";
    //整个数据库的表结构json格式
    String DATASOURCEINFO = "datasourceInfo";
    //整个数据库的表数量
    String TABLESCOUNT = "tablesCount";
    int MAX_IMUM = 10000;
    String[] WILDCARD_Arr = {"%", "_"};

    /**
     * 根据uri获取jdbc连接
     *
     * @param uri
     * @return
     */
    default String geturi(String uri) {
        //根据uri获取jdbc连接
        return uri.substring(0, uri.indexOf(SEPARATOR));
    }

    /**
     * 获取用户名
     *
     * @param uri
     * @return
     */
    static String getUsername(String uri) {
        //根据uri获取username
        String temp = uri.substring(uri.indexOf("username=") + "username=".length());
        String username = temp.substring(0, temp.indexOf(SEPARATOR));
        return username;
    }

    /**
     * 获取表空间
     * @param uri
     * @return
     */
    static String getSchema(String uri) {
        if (StringUtils.isBlank(uri) || !uri.contains("schema=")){
            return null;
        }
        //根据uri获取username
        int index = uri.indexOf("schema=") + "schema=".length();
        String temp = uri.substring(index);
        if (!uri.substring(index).contains(SEPARATOR)) {
            return temp;
        } else {
            return temp.substring(0, temp.indexOf(SEPARATOR));
        }
    }

    /**
     * 获取密码
     *
     * @param uri
     * @return
     */
    static String getPassword(String uri) {
        //根据uri获取password
        String temp = uri.substring(uri.indexOf("password=") + "password=".length());
        String password;
        if (!temp.endsWith(SEPARATOR)) {
            password = temp;
        } else {
            password = temp.substring(0, temp.indexOf(SEPARATOR));
        }
        SM4Utils sm4 = new SM4Utils();
        return sm4.decryptData_ECB(password);
    }

    /**
     * 获取连接url
     *
     * @param uri
     * @return
     */
    static String getConnUrl(String uri) {
        String[] split = uri.split("\\|");
        if (Objects.nonNull(split)) {
            return split[0];
        }
        return null;
    }

    default Long getPageCountByMaxImum(Long total, int pageSize) {
        if (total == 0L) {
            return 0L;
        } else if (total % (long) pageSize > 0L) {
            return Math.min((total / (long) pageSize + 1L), (MAX_IMUM / pageSize + 1L));
        } else {
            return Math.min((total / (long) pageSize), MAX_IMUM / pageSize);
        }
    }

    default String getFilelds(String fields){
        return StringUtils.isBlank(fields)? " * " : fields;
    }

    default String getDatabaseName(String uri){
        String s = StrUtil.subBefore(uri, "?", false);
        return StrUtil.subAfter(s, "/", true);
    }

    String getPageParamBySql(String querySql, Integer pageNum, Integer pageSize);

    String parseSql(String content, Map<String, String> kvs);

    String getFileld(String field);

    default Boolean isContainWildcard(String content, String key){
        String front = content.substring(0, content.indexOf(key));
        String end = content.substring(front.length() + key.length(), content.length());
        for (String wildcard: WILDCARD_Arr) {
            if (front.endsWith(wildcard) || end.startsWith(wildcard)){
                return true;
            }
        }
        return false;
    }
}
