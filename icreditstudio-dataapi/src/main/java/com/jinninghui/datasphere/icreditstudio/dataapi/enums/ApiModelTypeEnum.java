package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ApiModelTypeEnum {

    SINGLE_TABLE_CREATE_MODEL(0, "单表生成模式"),
    SQL_CREATE_MODEL(1, "sql生成模式"),
    CHAIN_CREATE_MODEL(2, "链上生成模式");

    private Integer code;
    private String msg;

    public static ApiModelTypeEnum findByModel(Integer Model) {
        if (Objects.nonNull(Model)) {
            for (ApiModelTypeEnum value : ApiModelTypeEnum.values()) {
                if (value.code.equals(Model)) {
                    return value;
                }
            }
        }
        return SQL_CREATE_MODEL;
    }
}
