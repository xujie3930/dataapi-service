package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisInterfaceInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.TokenInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthConfigService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthSaveRequest;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private IcreditApiBaseService apiBaseService;
    @Autowired
    private IcreditAppService appService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<Boolean> saveDef(String userId, AuthSaveRequest request) {
        if (CollectionUtils.isEmpty(request.getApiId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
        }
        IcreditAuthConfigEntity authConfigEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());
        authConfigService.save(authConfigEntity);
        //同时保存授权信息到redis：
        // key:API_PATH + API版本
        // value:应用token
        IcreditAppEntity appEntity = appService.getById(request.getAppId());
        for (String apiId : request.getApiId()) {
            IcreditAuthEntity authEntity = new IcreditAuthEntity();
            authEntity.setAppId(request.getAppId());
            authEntity.setApiId(apiId);
            authEntity.setAuthConfigId(authConfigEntity.getId());
            save(authEntity);
            IcreditApiBaseEntity apiBaseEntity = apiBaseService.getById(apiId);
            Object redisInfo = redisTemplate.opsForValue().get(apiBaseEntity.getPath() + apiBaseEntity.getApiVersion());
            if (Objects.isNull(redisInfo)){
                RedisInterfaceInfo info = new RedisInterfaceInfo();
                TokenInfo tokenInfo = new TokenInfo(appEntity.getToken(), request.getPeriodBegin());
                info.getTokenList().add(tokenInfo);
                redisTemplate.opsForValue().set(apiBaseEntity.getPath() + apiBaseEntity.getApiVersion(), JSON.toJSONString(info), 5, TimeUnit.MINUTES);
            }else {
                RedisInterfaceInfo info = (RedisInterfaceInfo) redisInfo;
                TokenInfo tokenInfo = new TokenInfo(appEntity.getToken(), request.getPeriodBegin());
                info.getTokenList().add(tokenInfo);
                redisTemplate.opsForValue().set(apiBaseEntity.getPath() + apiBaseEntity.getApiVersion(), JSON.toJSONString(info), 5, TimeUnit.MINUTES);
            }
        }
        return BusinessResult.success(true);
    }
}
