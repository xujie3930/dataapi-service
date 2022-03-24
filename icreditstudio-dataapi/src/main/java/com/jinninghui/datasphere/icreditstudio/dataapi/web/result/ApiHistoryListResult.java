package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;
import org.apache.htrace.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@Data
public class ApiHistoryListResult {

    private String apiHiId;
    private Integer apiVersion;
    private String publishUser;
    private Date publishTime;
    private Integer publishStatus;
    private String name;
    //数据来源：0-内部，1-外部
    private Integer interfaceSource;
}
