package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author xujie
 * @description 业务流程保存参数
 * @create 2022-02-21 12:08
 **/
@Data
public class WorkFlowSaveRequest extends BaseEntity {
    //业务流程名称
    @Length(min = 2, max = 50, message = "业务流程名称为2~50个字")
    private String name;
    //业务描述
    private String desc;

}
