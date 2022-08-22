package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xujie
 * @description 转为map的工具类
 * @create 2022-03-01 15:01
 **/
public class MapUtils {

    private static String UTF8 = "UTF-8";
    private static String PERCENT_CHAR = "%";
    private static String DOUBLE_PERCENT_CHAR = "%%";
    public static final String PARAM_ATTRIBUTE = "param";

    public static Map<String, Object> str2Map(String str){
        Map<String, Object> map = new HashMap();
        if (StringUtils.isBlank(str)){
            return map;
        }
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

    public static Map<String, Object> getRequestParamMap(HttpServletRequest request)
    {
        Map<String,Object> params = new HashMap<>();
        BufferedReader br;
        try {
            br = request.getReader();
            String str, wholeParams = "";
            while((str = br.readLine()) != null){
                wholeParams += str;
            }
            if(StringUtils.isNotBlank(wholeParams)){
                params = JSON.parseObject(wholeParams,Map.class);
            }
        } catch (IOException e) {
            throw new AppException("格式错误");
        }
        request.setAttribute(PARAM_ATTRIBUTE, params);
        return params;
    }


    public static List<String> mapKeyToList(Map map){
        List<String> keyList = (List<String>) map.keySet()
                .stream()
                .collect(Collectors.toList());
        return keyList;
    }
    public static void main(String[] args) {
        /*String str = "a=b&c=d&e=f";
        Map map = str2Map(str);
        System.out.println(map);*/
        Map<String, String> map = new HashMap<>();
        map.put("keya", "111");
        map.put("keyb", "222");
        List<String> str = mapKeyToList(map);
        System.out.println(map);
    }

    public static Map<String, Object> convertParams(String queryString) {
        Map<String, Object> paramMap = MapUtils.str2Map(queryString);
        try {
            for (String key : paramMap.keySet()) {
                String value = String.valueOf(paramMap.get(key));
                if(value.startsWith(DOUBLE_PERCENT_CHAR) && value.endsWith(PERCENT_CHAR)){//like 的参数值 -- %xxx%
                    paramMap.put(key, PERCENT_CHAR + URLDecoder.decode(value.substring(1, value.lastIndexOf(PERCENT_CHAR)),UTF8) + PERCENT_CHAR);
                }else if(value.startsWith(DOUBLE_PERCENT_CHAR)){//like 的参数值 -- %xxx
                    paramMap.put(key, PERCENT_CHAR + URLDecoder.decode(value.substring(1),UTF8));
                }else if(value.endsWith(PERCENT_CHAR)){//like 的参数值 -- xxx%
                    paramMap.put(key, URLDecoder.decode(value.substring(0, value.lastIndexOf(PERCENT_CHAR)),UTF8) + PERCENT_CHAR);
                }else{
                    paramMap.put(key, URLDecoder.decode(value, UTF8));
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return paramMap;
    }
}
