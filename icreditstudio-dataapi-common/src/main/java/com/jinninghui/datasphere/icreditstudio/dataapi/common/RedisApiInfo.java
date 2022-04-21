package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.Data;

import java.util.List;

@Data
public class RedisApiInfo {

    private String apiId;
    private Integer apiType;
    private Integer databaseType;
    private String apiName;
    private String url;
    private String userName;
    private String password;
    private String requiredFields;
    private String responseFields;
    private String querySql;
    private List<RegisterApiParamInfo> registerApiParamInfoList;
    private String reqHost;
    private String reqPath;
}
