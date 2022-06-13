package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ApiHisOverrideEnum {

    DEFAULT(0, "不覆盖，生成新版本"),
    OVERRIDE(1, "覆盖旧版本");

    private Integer code;
    private String msg;
}
