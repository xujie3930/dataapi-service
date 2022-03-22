package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

import java.util.List;

@Data
public class DatasourceApiSaveRequest {

    private String id;
    private Integer saveType;
    private Integer type;
    private String name;
    private String requestType;
    private String responseType;
    private String path;
    private String apiGroupId;
    private String desc;
    private Integer apiVersion;
    private String reqHost;
    private String reqPath;
    private String apiHiId;//历史版本api主键id（历史列表编辑使用）
    private String apiBaseId;//apiId（历史列表编辑使用）

    private DatasourceApiGenerateSaveRequest apiGenerateSaveRequest;
    private List<DatasourceApiParamSaveRequest> apiParamSaveRequestList;
    private List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList;
    private List<RegisterResponseParamSaveRequest> registerResponseParamSaveRequestList;
}
