package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenTypeEnum {

    LONG_TIME(0, -1, "长期"),
    EIGHT_HOURS(1, 8, "8小时"),
    CUSTOM(2, 0, "自定义")
    ;

    private Integer code;
    private Integer period;
    private String msg;

}
