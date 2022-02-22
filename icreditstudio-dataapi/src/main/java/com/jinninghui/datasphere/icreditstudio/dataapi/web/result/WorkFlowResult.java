package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xujie
 * @description 业务流程返回参数
 * @create 2022-02-22 14:07
 **/
@Data
@AllArgsConstructor
public class WorkFlowResult {
    private String workFlowName;
    private String workFlowId;
    private List<ApiGroupResult> apiGroup = new LinkedList<>();

    public WorkFlowResult(String workFlowName, String workFlowId) {
        this.workFlowName = workFlowName;
        this.workFlowId = workFlowId;
    }
}
