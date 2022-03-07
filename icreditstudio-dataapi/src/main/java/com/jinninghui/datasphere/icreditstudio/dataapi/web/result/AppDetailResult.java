package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

@Data
public class AppDetailResult {

    private String generateId;
    private String name;
    private String secretContent;
    private String appGroupName;
    private String desc;
    private String allowIp;
    private Integer certificationType;
    private Integer isEnable;
    private String period;
    private Long createTime;
    private String createBy;
    private ApiResult apiResult;
    private AuthResult authResult;

}
