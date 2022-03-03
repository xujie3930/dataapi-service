package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AppEnableEnum {
    //true启用,false未启用

    NOT_ENABLE(0, "未启用"),
    ENABLE(1, "启用");

    private final Integer code;
    private final String msg;
}
