package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum InterfaceSourceEnum {

    IN_SIDE(0, "内部"),
    OUT_SIDE(1, "外部");

    private Integer code;
    private String msg;


}
