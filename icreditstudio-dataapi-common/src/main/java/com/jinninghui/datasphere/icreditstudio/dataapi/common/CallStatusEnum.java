package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CallStatusEnum {
    //0请求中,1成功,2失败

    CALL_ON(0, "请求中"),
    CALL_SUCCESS(1, "成功"),
    CALL_FAIL(2, "失败");

    private final Integer code;
    private final String msg;
}
