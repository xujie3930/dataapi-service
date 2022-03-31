package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

import java.util.List;

@Data
public class AuthInfoResult {

    private String appId;
    private String appName;
    private List<ApiCascadeInfoResult> apiCascadeInfoStrList;
    private List<ApiCascadeInfoResult> noApiCascadeInfoStrList;
    private AuthResult authResult;
    private Integer infoType;

}
