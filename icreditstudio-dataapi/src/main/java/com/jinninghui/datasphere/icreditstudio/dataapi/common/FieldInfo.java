package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.Data;

@Data
public class FieldInfo {

    private String fieldName;
    private String fieldType;
    private Integer isRequestField;
    private Integer isResponseField;
    private Integer isRequiredField;
    private String desc;

}
