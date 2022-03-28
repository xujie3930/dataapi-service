package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 0 -注册API，1 -数据源生成API
 */
@AllArgsConstructor
@Getter
public enum ApiTypeEnum {

    API_REGISTER(0, "注册API"),
    API_GENERATE(1, "数据源生成API"),
    ;

    private Integer code;
    private String msg;

    public static ApiTypeEnum findByType(Integer type) {
        if (Objects.nonNull(type)) {
            for (ApiTypeEnum value : ApiTypeEnum.values()) {
                if (value.code.equals(type)) {
                    return value;
                }
            }
        }
        return API_GENERATE;
    }
}
