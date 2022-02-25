package com.jinninghui.datasphere.icreditstudio.dataapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ApiSaveStatusEnum {

    API_SAVE(0, "保存"),
    API_PUBLISH(1, "提交发布")
    ;

    private Integer code;
    private String desc;

}
