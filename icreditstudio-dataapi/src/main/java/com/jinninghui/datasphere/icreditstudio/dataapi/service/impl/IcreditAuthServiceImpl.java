package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthConfigService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthSaveRequest;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 授权表 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditAuthServiceImpl extends ServiceImpl<IcreditAuthMapper, IcreditAuthEntity> implements IcreditAuthService {

    @Autowired
    private IcreditAuthConfigService authConfigService;
    @Override
    public BusinessResult<Boolean> saveDef(String userId, AuthSaveRequest request) {
        if (CollectionUtils.isEmpty(request.getApiId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
        }
        IcreditAuthConfigEntity authConfigEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());
        authConfigService.save(authConfigEntity);
        for (String apiId : request.getApiId()) {
            IcreditAuthEntity authEntity = new IcreditAuthEntity();
            authEntity.setAppId(request.getAppId());
            authEntity.setApiId(apiId);
            authEntity.setAuthConfigId(authConfigEntity.getId());
            save(authEntity);
        }
        return BusinessResult.success(true);
    }
}
