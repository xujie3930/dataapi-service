package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

import java.util.Date;

@Data
public class LogListQueryResult {

    private String id;
    private String apiName;
    private String apiPath;
    private String appName;
    private String callIp;
    private Integer apiVersion;
    private Date callBeginTime;
    private Integer runTime;
    private Integer callStatus;
}
