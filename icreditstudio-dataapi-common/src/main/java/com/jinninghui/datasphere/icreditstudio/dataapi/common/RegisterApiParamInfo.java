package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.Data;

@Data
public class RegisterApiParamInfo {

    private String defaultValue;
    private Integer isRequest;
    private Integer isResponse;
    private String fieldName;
    private Integer required;
    private String fieldType;

}
