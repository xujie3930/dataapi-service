package com.jinninghui.datasphere.icreditstudio.dataapi.feign.result;

public class DataSourceInfoRequest {

    private String datasourceId;

    public DataSourceInfoRequest(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }
}
