package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthInfoResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.util.List;

/**
 * <p>
 * 授权表 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAuthService extends IService<IcreditAuthEntity> {

    BusinessResult<Boolean> saveDef(String userId, AuthSaveRequest request);

    List<IcreditAuthEntity> findByAppId(String appId);

    BusinessResult<AuthInfoResult> authInfo(AuthInfoRequest request);
}
