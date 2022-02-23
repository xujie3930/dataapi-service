package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequiredFiledEnum {

    IS_REQUIRED_FIELD(0, "是必填字段"),
    NOT_IS_REQUIRED_FIELD(1, "不是必填字段")
    ;
    private Integer code;
    private String msg;

}
