package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @author xujie
 * @description ResultSetToList
 * @create 2021-11-30 11:09
 **/
public class ResultSetToListUtils {
    public static<T> List<? extends T> convertList(ResultSet rs, String responseParam) throws SQLException {
        List list = new ArrayList();
        //获取键名
        ResultSetMetaData md = rs.getMetaData();
        //获取行的数量
        int columnCount = md.getColumnCount();
        rs.beforeFirst();
        while (rs.next()) {
            Map rowData = new IdentityHashMap();
            String[] params = responseParam.split(",");
            for (int i = 1; i <= columnCount; i++) {
                rowData.put(params[i-1], rs.getString(i));
            }
            list.add(rowData);
        }
        return list;
    }
}
