package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthEffectiveTimeEnum {

    SORT_TIME(0L, "短期"),
    LONG_TIME(-1L, "永久")
    ;

    private Long code;
    private String msg;

}
