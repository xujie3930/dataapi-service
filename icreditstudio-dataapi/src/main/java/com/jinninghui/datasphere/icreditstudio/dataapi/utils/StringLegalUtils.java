package com.jinninghui.datasphere.icreditstudio.dataapi.utils;

import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;

/**
 * @author xujie
 * @description 校验输入字符串合法性
 * @create 2022-02-22 18:56
 **/
public class StringLegalUtils {

    public static void checkLegalName(String name) {
        /*boolean legal = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isLegal(name);
        if (!legal){
            throw new AppException("输入包含除中文汉字、英文字母、数字、英文格式的下划线之外的字符");
        }
        boolean front = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isFrontWithNumOrLetters(name);
        if (!front){
            throw new AppException("请输入以英文字母或者汉字开头的2~50字的名称");
        }*/
    }

    public static void main(String[] args) {
        String str = "abcAbc123";
        checkLegalName(str);
    }
}
