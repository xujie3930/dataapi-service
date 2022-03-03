package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

@Data
public class LogDetailResult {

    private String apiName;
    private String apiPath;
    private String appName;
    private String callIp;
    private Integer apiVersion;
    private Integer apiType;
    private String requestProtocol;
    private String requestType;
    private String responseType;
    private String requestParam;
    private String responseParam;
    private Long callBeginTime;
    private Long callEndTime;
    private Long runTime;
    private Integer callStatus;
    private String exceptionDetail;

}
