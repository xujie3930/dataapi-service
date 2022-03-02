package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallCountEnum {

    CALL_FINITE_TIMES(0, "有限次"),
    CALL_INFINITE_TIMES(-1, "无限次")
    ;

    private Integer code;
    private String msg;

}
