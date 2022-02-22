package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditWorkFlowEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务流程 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditWorkFlowService extends IService<IcreditWorkFlowEntity> {

    Boolean hasExit(WorkFlowSaveRequest request);

    BusinessResult<String> saveDef(String userId, WorkFlowSaveRequest request);

    BusinessResult<List<IcreditWorkFlowEntity>> getList();

    BusinessResult<Boolean> rearrangement(String userId, Map<String, String[]> request);

    List<WorkFlowResult> searchFromName(WorkFlowSaveRequest request);
}
