package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author xujie
 * @description 业务流程保存参数
 * @create 2022-02-21 12:08
 **/
@Data
public class WorkFlowSaveRequest extends BaseEntity {
    //业务流程名称
    @NotBlank(message = "20000018")
    @Length(min = 2, max = 50, message = "20000015")
    private String name;
    //业务描述
    @Length(max = 250, message = "20000017")
    private String desc;

}
