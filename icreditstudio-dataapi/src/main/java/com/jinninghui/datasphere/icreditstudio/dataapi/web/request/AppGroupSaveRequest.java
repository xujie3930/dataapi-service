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
public class AppGroupSaveRequest {
    private String id;
    @NotBlank(message = "20000025")
    @Length(max = 50, message = "20000026")
    private String name;
    private String generateId;
    @Length(max = 250, message = "20000027")
    private String desc;
}
