package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiPublishStatusEnum {

    WAIT_PUBLISH(0, "待发布"),
    NO_PUBLISHED(1, "未发布"),
    PUBLISHED(2, "已发布")
    ;

    private Integer code;
    private String desc;
}
