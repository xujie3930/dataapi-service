package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xujie
 * @since 2021-11-25
 */
@AllArgsConstructor
@Getter
public enum RquestWayEnum {
    POST(0, "POST"),
    GET(1, "GET"),
    ;
    private Integer code;
    private String desc;

    public static RquestWayEnum find(Integer code) {
        for (RquestWayEnum value : RquestWayEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
