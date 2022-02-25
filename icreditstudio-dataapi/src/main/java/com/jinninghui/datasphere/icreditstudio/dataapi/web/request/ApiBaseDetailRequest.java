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
    @NotBlank(message = "API详情id不能为空")
    private String id;
}
