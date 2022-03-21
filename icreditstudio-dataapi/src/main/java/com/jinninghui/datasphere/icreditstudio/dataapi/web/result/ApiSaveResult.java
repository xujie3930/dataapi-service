package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.RegisterRequestParamSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.RegisterResponseParamSaveRequest;
import lombok.Data;

import java.util.List;

@Data
public class ApiSaveResult {

    private String id;
    private String apiHiId;
    private ApiGenerateSaveResult apiGenerateSaveRequest;
    private List<ApiParamSaveResult> apiParamSaveRequestList;
    private List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList;
    private List<RegisterResponseParamSaveRequest> registerResponseParamSaveRequestList;

}
