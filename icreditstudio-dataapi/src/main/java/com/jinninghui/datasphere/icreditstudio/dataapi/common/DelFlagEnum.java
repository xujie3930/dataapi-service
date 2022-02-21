package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xujie
 * @description 状态枚举
 * @create 2022-02-15 16:35
 **/
@AllArgsConstructor
@Getter
public enum DelFlagEnum {
    ENA_BLED(0, "未删除"),
    DIS_ABLED(1, "已删除"),
    ;

    private Integer code;
    private String desc;
}
