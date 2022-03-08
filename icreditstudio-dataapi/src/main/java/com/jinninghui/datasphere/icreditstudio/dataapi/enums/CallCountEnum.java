package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallCountEnum {

    CALL_FINITE_TIMES(0, 0),
    CALL_INFINITE_TIMES(-1, 1)
    ;

    private Integer code;
    private Integer callTime;

}
