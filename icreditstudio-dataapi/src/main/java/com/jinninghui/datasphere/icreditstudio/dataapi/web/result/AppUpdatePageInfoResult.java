package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

@Data
public class AppUpdatePageInfoResult {

    private String id;
    private String generateId;
    private String name;
    private String secretContent;
    private String appGroupName;
    private String appGroupId;
    private String desc;
    private String allowIp;
    private Integer certificationType;
    private Integer isEnable;
    private Integer period;
    //0-长期，1-8小时，2-自定义
    private Integer tokenType;

}
