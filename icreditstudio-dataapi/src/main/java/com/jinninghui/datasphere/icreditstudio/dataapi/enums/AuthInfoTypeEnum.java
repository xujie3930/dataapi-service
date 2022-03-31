package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthInfoTypeEnum {

    ADD(0, "新增"),
    UPDATE(1, "编辑"),
    ;

    private Integer code;
    private String msg;

}
