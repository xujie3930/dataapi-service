package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

/**
 * @author xujie
 * @description API参数返回值
 * @create 2022-02-23 17:05
 **/
@Data
public class APIParamResult {
    //是否为返回参数，0-否，1-是
    private Integer isResponse;
    //是否为请求参数，0-否，1-是
    private Integer isRequest;
    //字段名称
    private String fieldName;
    //字段类型
    private String fieldType;
    //是否必填：0-否，1-是
    private Integer required;
    //说明
    private String desc;
}
