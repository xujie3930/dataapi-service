package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

import java.util.Date;

@Data
public class ApiHistoryListResult {

    private String apiHiId;
    private Integer apiVersion;
    private String publishUser;
    private Date publishTime;
    private Integer publishStatus;
    private String name;
    private Integer interfaceSource;

}
