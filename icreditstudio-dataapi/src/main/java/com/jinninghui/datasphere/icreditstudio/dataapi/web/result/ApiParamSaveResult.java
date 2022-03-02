package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

@Data
public class ApiParamSaveResult {

    private String id;

    private String fieldName;

    private String tableName;

    private String fieldType;

    private Integer required;

    private String desc;

    private Integer isRequest;

    private Integer isResponse;

}
