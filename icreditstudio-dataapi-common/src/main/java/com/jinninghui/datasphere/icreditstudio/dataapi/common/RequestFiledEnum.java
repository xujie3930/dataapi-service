package com.jinninghui.datasphere.icreditstudio.dataapi.common;

/**
 * @author xujie
 * @description 1
 * @create 2022-03-21 15:44
 **/
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RequestFiledEnum {

    IS_REQUEST_FIELD(0, "是入参字段"),
    NOT_IS_REQUEST_FIELD(1, "不是入参字段")
    ;
    private Integer code;
    private String msg;

}
