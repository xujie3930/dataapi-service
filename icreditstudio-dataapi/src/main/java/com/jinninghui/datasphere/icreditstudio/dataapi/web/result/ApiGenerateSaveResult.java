package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

@Data
public class ApiGenerateSaveResult {

    private String id;

    private Integer model;

    private String sql;

    private String datasourceId;

    private String tableName;

    private Integer databaseType;

}
