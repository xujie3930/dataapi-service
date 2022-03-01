package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

import java.util.List;

@Data
public class AppGroupQueryListResult {

    private String id;
    private String name;
    private String generateId;
    private Integer certificationType;
    private Integer isEnable;
    private Integer period;
    private String createBy;
    private Long createTime;
    private String desc;
    private List<AppQueryListResult> children;

}
