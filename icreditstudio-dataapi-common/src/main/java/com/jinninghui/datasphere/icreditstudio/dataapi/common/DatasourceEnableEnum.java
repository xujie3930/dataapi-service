package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DatasourceEnableEnum {
    //true启用,false未启用

    ENABLE(0, "启用"),
    NOT_ENABLE(1, "未启用");

    private final Integer code;
    private final String msg;
}
