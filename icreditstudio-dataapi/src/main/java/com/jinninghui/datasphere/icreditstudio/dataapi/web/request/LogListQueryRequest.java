package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BusinessBasePageForm;
import lombok.Data;

import java.io.Serializable;

@Data
public class LogListQueryRequest extends BusinessBasePageForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String apiName;
    private Integer apiVersion;
    private String appName;
    private String callBeginTime;
    private String callEndTime;
    private Integer callStatus;
    private Integer pageStartNum;
}
