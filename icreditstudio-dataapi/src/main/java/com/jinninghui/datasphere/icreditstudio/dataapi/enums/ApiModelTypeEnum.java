package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiModelTypeEnum {

    SINGLE_TABLE_CREATE_MODEL(0, "单表生成模式"),
    SQL_CREATE_MODEL(1, "sql生成模式"),
    CHAIN_CREATE_MODEL(2, "链上生成模式")
    ;

    private Integer code;
    private String msg;

}
