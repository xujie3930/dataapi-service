package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.Data;

@Data
public class FieldInfo {

    private String fieldName;
    private String fieldType;
    private Integer isRequest;
    private Integer isResponse;
    private Integer required;
    private String desc;

}
