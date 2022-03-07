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
    @NotBlank(message = "20000029")
    private String workId;
    //api分组名称
    @NotBlank(message = "20000011")
    @Length(min = 2, max = 50, message = "20000030")
    private String name;
    //api分组描述
    @Length(max = 200, message = "20000031")
    private String desc;
}
