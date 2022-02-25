package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

import java.util.List;

@Data
public class ApiSaveResult {

    private String id;
    private ApiGenerateSaveResult apiGenerateSaveRequest;
    private List<ApiParamSaveResult> apiParamSaveRequestList;

}
