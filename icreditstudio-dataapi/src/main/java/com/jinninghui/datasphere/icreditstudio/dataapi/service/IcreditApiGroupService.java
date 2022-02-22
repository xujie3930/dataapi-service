package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiGroupListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiGroupSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGroupResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * API分组 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiGroupService extends IService<IcreditApiGroupEntity> {

    BusinessResult<String> saveDef(String userId, ApiGroupSaveRequest request);

    Boolean hasExit(WorkFlowSaveRequest request);

    BusinessResult<List<IcreditApiGroupEntity>> getList(ApiGroupListRequest request);

    List<ApiGroupResult> getByWorkId(@Param("workFlowId") String workFlowId);

    List<IcreditApiGroupEntity> searchFromName(String name);
}
