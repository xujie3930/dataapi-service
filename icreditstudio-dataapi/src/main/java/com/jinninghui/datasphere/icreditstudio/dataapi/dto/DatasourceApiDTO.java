package com.jinninghui.datasphere.icreditstudio.dataapi.dto;

import lombok.Data;

@Data
public class DatasourceApiDTO {

    private Integer apiVersion;
    private String apiName;
    private String apiDesc;
    private String datasourceId;
    private String tableName;
    private String querySql;

}
