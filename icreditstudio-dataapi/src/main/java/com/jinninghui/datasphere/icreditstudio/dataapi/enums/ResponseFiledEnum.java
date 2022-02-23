package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  ResponseFiledEnum {

    IS_RESPONSE_FIELD(0, "是返回字段"),
    NOT_IS_RESPONSE_FIELD(1, "不是返回字段")
    ;
    private Integer code;
    private String msg;

}
