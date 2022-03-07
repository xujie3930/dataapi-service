package com.jinninghui.datasphere.icreditstudio.dataapi.utils;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;

/**
 * @author xujie
 * @description 校验输入字符串合法性
 * @create 2022-02-22 18:56
 **/
public class StringLegalUtils {

    public static void checkLegalName(String name) {
        boolean legal = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isLegal(name);
        if (!legal) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000014.getCode());
        }
        boolean front = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isFrontWithEnglish(name) ||
                com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.isFrontWithChinese(name);
        if (!front) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000015.getCode());
        }
    }

    public static void checkLegalNameForApp(String name) {
        if (!name.matches("[a-zA-Z0-9\u4e00-\u9fa5]{1,50}")) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000016.getCode());
        }
    }

    public static void main(String[] args) {
        String str = "0.0.0.0";
        checkLegalAllowIpForApp(str);
    }

    public static void checkLegalSecretContentForApp(String secretContent) {
        char[] chars = secretContent.toCharArray();
        int engSize = 0;
        int zhSize = 0;
        for (char c : chars) {
            if((c >= 'A' && c <= 'z')){
                engSize++;
            }
            if(c >= '0' && c <= '9'){
                zhSize++;
            }
        }
        if (0 == zhSize || 0 == engSize || 16 != (engSize + zhSize)) {
            throw new AppException("密钥只能包含数字、英文字母且必须包含数字和英文字母，长度为16");
        }
    }

    public static void checkLegalDescForApp(String desc) {
        if(desc.length() > 250){
            throw new AppException("描述文字必须在250以内");
        }
    }

    public static void checkLegalAllowIpForApp(String allowIp) {
        if (!allowIp.matches("[0-9\\,\\.]{0,255}")) {
            throw new AppException("IP白名单只能包含【数字】、【.】、【,】，且长度不能超过255");
        }
        String[] ipArr = allowIp.split(",");
        for (String ip : ipArr) {
            if(!StringUtils.isEmpty(ip) && !ip.matches("([0-9]|[0-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}")){
                throw new AppException("IP白名单中有不规范的IP地址");
            }
        }
    }
}
