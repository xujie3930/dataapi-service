package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckQuerySqlRequest {

    private String datasourceId;
    private String sql;

}
