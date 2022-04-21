package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

@Data
public class DatasourceApiGenerateSaveRequest {

    private String id;
    private Integer model;
    private String datasourceId;
    private String sql;
    private String tableName;
    private Integer databaseType;//数据库类型:1-mysql,2-oracle,3-pg
}
