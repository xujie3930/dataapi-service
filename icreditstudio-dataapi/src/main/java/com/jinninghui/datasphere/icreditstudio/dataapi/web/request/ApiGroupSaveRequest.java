package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author xujie
 * @description API分组保存参数
 * @create 2022-02-21 16:23
 **/
@Data
public class ApiGroupSaveRequest {
    //业务流程id
    @NotBlank(message = "业务流程id不能为空!")
    private String workId;
    //api分组名称
    @NotBlank(message = "分组名称不能为空")
    @Length(min = 2, max = 50, message = "请输入以英文字母或者汉字开头的2~50字的分组名称")
    private String name;
    //api分组描述
    @Length(max = 200, message = "分组描述200字以内")
    private String desc;
}
