package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

@Data
public class DatasourceApiParamSaveRequest {

    private String id;
    private String fieldName;
    private String fieldType;
    private Integer required;
    private String desc;
    private Integer isRequest;
    private Integer isResponse;
}
