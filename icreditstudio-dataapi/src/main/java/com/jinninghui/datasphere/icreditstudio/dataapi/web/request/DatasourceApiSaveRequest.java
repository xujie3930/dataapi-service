package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

import java.util.List;

@Data
public class DatasourceApiSaveRequest {

    private String id;
    private Integer type;
    private String name;
    private String requestType;
    private String responseType;
    private String path;
    private String apiGroupId;
    private String desc;
//    private String apiGroupName;
    private Integer apiVersion = 1;

    private DatasourceApiGenerateSaveRequest apiGenerateSaveRequest;
    private List<DatasourceApiParamSaveRequest> apiParamSaveRequestList;
}
