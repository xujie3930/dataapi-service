package com.jinninghui.datasphere.icreditstudio.dataapi.utils;

import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;

/**
 * @author xujie
 * @description 校验输入字符串合法性
 * @create 2022-02-22 18:56
 **/
public class StringLegalUtils {

    public static void checkLegalName(String name) {
        boolean legal = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isLegal(name);
        if (!legal) {
            throw new AppException("包含不规范字符，请重新输入");
        }
        boolean front = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isFrontWithEnglish(name) ||
                com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isFrontWithChinese(name);
        if (!front) {
            throw new AppException("请输入以英文字母或者汉字开头的2~50字的名称");
        }
    }

    public static void checkLegalNameForApp(String name) {
        boolean legal = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isLegal(name);
        if (!legal) {
            throw new AppException("包含不规范字符，请重新输入");
        }
    }

    public static void main(String[] args) {
        String str = "分组8";
        checkLegalName(str);
    }
}
