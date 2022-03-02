package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppQueryListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppGroupSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.CheckAppGroupNameRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppGroupQueryListResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.util.List;

/**
 * <p>
 * 应用分组 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAppGroupService extends IService<IcreditAppGroupEntity> {

    BusinessResult<String> saveDef(String userId, AppGroupSaveRequest request);

    BusinessResult<List<AppGroupQueryListResult>> getList(AppQueryListRequest request);

    BusinessResult<Boolean> checkAppGroupName(CheckAppGroupNameRequest request);

    BusinessResult<String> generateId();

    String findNameById(String appGroupId);
}
