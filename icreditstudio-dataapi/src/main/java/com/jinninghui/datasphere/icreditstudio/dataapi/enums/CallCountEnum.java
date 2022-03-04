package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallCountEnum {

    CALL_FINITE_TIMES(0L, "有限次"),
    CALL_INFINITE_TIMES(-1L, "无限次")
    ;

    private Long code;
    private String msg;

}
