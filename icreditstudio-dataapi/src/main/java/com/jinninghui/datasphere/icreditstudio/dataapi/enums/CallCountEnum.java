package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallCountEnum {

    CALL_FINITE_TIMES(0L, 0),
    CALL_INFINITE_TIMES(-1L, 1)
    ;

    private Long code;
    private Integer callTime;

}
