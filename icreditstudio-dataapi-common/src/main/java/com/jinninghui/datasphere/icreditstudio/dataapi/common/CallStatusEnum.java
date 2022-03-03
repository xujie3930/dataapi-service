package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum CallStatusEnum {
    //0请求中,1成功,2失败

    CALL_ON(0, "请求中"),
    CALL_SUCCESS(1, "成功"),
    CALL_FAIL(2, "失败");

    private Integer code;
    private String msg;
}
