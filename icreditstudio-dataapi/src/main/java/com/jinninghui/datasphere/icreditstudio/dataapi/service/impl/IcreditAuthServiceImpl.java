package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppAuthInfo;
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
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.UUID;
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
                saveToRedis(request, appEntity, apiBaseEntity, info, authConfigEntity);
            }else {
                RedisInterfaceInfo info = JSON.parseObject(redisInfo.toString(), RedisInterfaceInfo.class);
                saveToRedis(request, appEntity, apiBaseEntity, info, authConfigEntity);
            }
        }
        return BusinessResult.success(true);
    }

    private void saveToRedis(AuthSaveRequest request, IcreditAppEntity appEntity, IcreditApiBaseEntity apiBaseEntity, RedisInterfaceInfo info, IcreditAuthConfigEntity authConfigEntity) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        TokenInfo tokenInfo = new TokenInfo(token, request.getPeriodEnd());
        info.getTokenList().add(tokenInfo);
        //根据API的PATH和版本，获取已授权的应用token列表，若过期则鉴权失败
        redisTemplate.opsForValue().set(apiBaseEntity.getPath() + apiBaseEntity.getApiVersion(), JSON.toJSONString(info));
        //根据appFlag，获取token
        Object redisObject = redisTemplate.opsForValue().get(appEntity.getAppFlag());
        if (Objects.isNull(redisObject)){
            AppAuthInfo appAuthInfo = JSON.parseObject(redisObject.toString(), AppAuthInfo.class);
            appAuthInfo.setToken(token);
            BeanCopyUtils.copyProperties(authConfigEntity, appAuthInfo);
            redisTemplate.opsForValue().set(appEntity.getAppFlag(), JSON.toJSONString(appAuthInfo));
        }
    }
}
