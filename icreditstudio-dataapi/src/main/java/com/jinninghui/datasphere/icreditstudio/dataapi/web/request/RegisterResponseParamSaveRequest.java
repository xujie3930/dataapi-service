package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

@Data
public class RegisterResponseParamSaveRequest {

    private String fieldName;
    private String fieldType;
    private String desc;
    private Integer isResponse = 0;
    private String defaultValue;
}
