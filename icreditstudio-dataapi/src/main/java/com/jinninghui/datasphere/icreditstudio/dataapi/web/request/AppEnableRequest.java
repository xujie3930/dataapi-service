package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author xujie
 * @description APP应用分组新增参数
 * @create 2022-02-24 16:40
 **/
@Data
public class AppEnableRequest {
    @NotBlank(message = "20000021")
    private String id;
    //是否启用(0-未启用,1-启用),默认1
    private Integer isEnable;
}
