package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum InterfaceSourceEnum {

    DATA_SERVICES(0, "数据服务"),
    DATA_RESOURCE(1, "资源管理");

    private Integer code;
    private String msg;


}
