package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author xujie
 * @description APP应用分组新增参数
 * @create 2022-02-24 16:40
 **/
@Data
public class AppGroupListRequest {
    @NotBlank(message = "应用分组名称不能为空")
    @Length(max = 50, message = "请输入以英文字母或者汉字开头的50字的名称")
    private String name;
}
