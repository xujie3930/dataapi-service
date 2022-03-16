package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

@Data
public class RegisterRequestParamSaveRequest {

    private String fieldName;
    private String fieldType;
    private Integer required;
    private String desc;
    private Integer isRequest = 0;
    private String defaultValue;
}
