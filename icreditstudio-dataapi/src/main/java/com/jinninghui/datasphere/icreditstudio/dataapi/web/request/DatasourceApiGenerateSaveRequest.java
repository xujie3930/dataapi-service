package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

@Data
public class DatasourceApiGenerateSaveRequest {

    private Integer model;
    private String datasourceId;
    private String sql;
    private String tableName;
}
