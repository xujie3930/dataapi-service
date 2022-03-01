package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

@Data
public class AppQueryListRequest {
    private String appGroupName;//应用分组名称
    private String appName;//应用名称
    private Integer certificationType;//应用认证方式
    private Integer isEnable;//应用启用状态
    private Integer period;//应用token有效期
}
