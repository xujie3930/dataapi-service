package com.jinninghui.datasphere.icreditstudio.dataapi.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AppQueryListDTO {

    private String id;
    private String name;
    private String generateId;
    private Integer certificationType;
    private Integer isEnable;
    private Integer period;
    private String createBy;
    private Date createTime;
    private String desc;
    private String appGroupId;

}
