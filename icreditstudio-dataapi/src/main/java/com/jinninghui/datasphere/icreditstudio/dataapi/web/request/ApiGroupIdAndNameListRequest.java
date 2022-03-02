package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class ApiGroupIdAndNameListRequest extends BaseEntity {
    //入参：业务流程id
    private List<String> workIds;
}
