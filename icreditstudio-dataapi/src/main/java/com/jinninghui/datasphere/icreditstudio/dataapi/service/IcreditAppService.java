package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppDetailRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppEnableRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.util.List;

/**
 * <p>
 * 应用 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAppService extends IService<IcreditAppEntity> {

    BusinessResult<String> saveDef(String userId, AppSaveRequest request);

    BusinessResult<Boolean> enableOrStop(AppEnableRequest request);

    Boolean hasExitByGenerateId(String generateId, String appGroupId);

    BusinessResult<AppDetailResult> detail(AppDetailRequest request);

    AuthResult generateAuthResultInfo(String authConfigId, Integer period);

    String findEnableAppIdByAppGroupIds(List<String> ids);

    List<String> getIdsByAppGroupIds(List<String> ids);
}
