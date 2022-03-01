package com.jinninghui.datasphere.icreditstudio.gateway.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xujie
 * @description 转为map的工具类
 * @create 2022-03-01 15:01
 **/
public class MapUtils {

    public static Map str2Map(String str){
        Map map = new HashMap();
        String[] allStrings = str.split("&");
        if (allStrings != null && allStrings.length != 0) {
            for (String strings : allStrings) {
                if (strings != null && strings.trim().length() != 0) {
                    String[] x = strings.split("=");
                    if (x != null && x.length == 2) {
                        map.put(x[0], strings.substring(strings.indexOf("=") + 1));
                    }
                }
            }
        }
        return map;
    }

    public static void main(String[] args) {
        String str = "a=b&c=d&e=f";
        Map map = str2Map(str);
        System.out.println(map);
    }
}
