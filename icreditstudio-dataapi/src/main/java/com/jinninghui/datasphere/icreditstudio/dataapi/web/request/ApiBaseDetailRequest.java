package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author xujie
 * @description API详情查询参数
 * @create 2022-02-22 15:39
 **/
@Data
public class ApiBaseDetailRequest {
    //API详情id
    @NotBlank(message = "20000019")
    private String id;
}
