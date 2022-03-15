package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGroupDelResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGroupResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.GroupIdAndNameResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * API分组 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiGroupService extends IService<IcreditApiGroupEntity> {

    BusinessResult<Map<String, String>> saveDef(String userId, ApiGroupSaveRequest request);

    Boolean hasExit(WorkFlowSaveRequest request);

    BusinessResult<List<IcreditApiGroupEntity>> getList(ApiGroupListRequest request);

    List<ApiGroupResult> getByWorkId(String workFlowId);

    List<IcreditApiGroupEntity> searchFromName(String name);

    BusinessResult<List<GroupIdAndNameResult>> getGroupListByWorkFlowId(ApiGroupIdAndNameListRequest request);

    BusinessResult<Boolean> renameById(ApiGroupRenameRequest request);

    List<String> getIdsByWorkId(String id);

    BusinessResult<ApiGroupDelResult> delById(ApiGroupDelRequest request);

    String getFirstApiGroupForWorkFlow(String workId);
}
