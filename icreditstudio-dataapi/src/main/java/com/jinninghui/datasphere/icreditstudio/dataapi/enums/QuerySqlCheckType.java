package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuerySqlCheckType {

    NEED_NOT_GET_TABLE_FIELD(0),
    NEED_GET_TABLE_FIELD(1)
    ;
    private Integer code;

}
