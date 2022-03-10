package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogCallStatusEnum {

    REQUESTING(0, "请求中"),
    SUCCEED(1, "成功"),
    FAILED(2, "失败")
    ;
    private Integer code;
    private String msg;

}
