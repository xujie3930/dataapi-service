package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SelectFiledEnum {

    FIELD_SELECTED(0, "已勾选"),
    FIELD_NOT_SELECTED(1, "未勾选")
    ;
    private Integer code;
    private String msg;

}
