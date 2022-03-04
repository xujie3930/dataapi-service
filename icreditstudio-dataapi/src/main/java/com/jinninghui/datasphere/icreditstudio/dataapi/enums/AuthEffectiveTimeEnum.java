package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthEffectiveTimeEnum {

    SORT_TIME(0L, 0),
    LONG_TIME(-1L, 1)
    ;

    private Long code;
    private Integer effective;

}
