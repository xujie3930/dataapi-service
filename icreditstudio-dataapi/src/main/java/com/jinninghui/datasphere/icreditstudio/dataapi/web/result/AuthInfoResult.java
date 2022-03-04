package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

import java.util.List;

@Data
public class AuthInfoResult {

    private String appId;
    private String appName;
    private List<List<ApiCascadeInfoResult>> apiCascadeInfoStrList;
    private AuthResult authResult;

}
