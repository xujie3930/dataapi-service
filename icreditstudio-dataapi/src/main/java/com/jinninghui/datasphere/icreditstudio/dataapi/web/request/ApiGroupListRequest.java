package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;

/**
 * @author xujie
 * @description 查看分组列表
 * @create 2022-02-21 16:34
 **/
@Data
public class ApiGroupListRequest extends BaseEntity {
    //入参：业务流程id
    private String workId;
}
