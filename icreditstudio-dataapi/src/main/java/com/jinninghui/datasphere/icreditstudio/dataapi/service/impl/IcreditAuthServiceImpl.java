package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisAppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ApiInfoDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.AuthEffectiveTimeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthConfigService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.*;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private IcreditAuthConfigService authConfigService;
    @Resource
    private IcreditAppService appService;
    @Resource
    private IcreditAuthService authService;
    @Resource
    private IcreditWorkFlowService workFlowService;
    @Resource
    private IcreditAuthMapper authMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<Boolean> saveDef(String userId, AuthSaveRequest request) {
        if(AuthEffectiveTimeEnum.SORT_TIME.getDurationType().equals(request.getDurationType()) && request.getAllowCall() < 0){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000036.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000036.getMessage());
        }
        if (CollectionUtils.isEmpty(request.getApiId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
        }
        IcreditAuthConfigEntity authConfigEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());
        authConfigService.saveOrUpdate(authConfigEntity);
        //同时保存授权信息到redis
        IcreditAppEntity appEntity = appService.getById(request.getAppId());

        List<IcreditAuthEntity> authList = authMapper.findByAppId(request.getAppId());

        List<String> cancelSelectedList = new ArrayList<>();
        for (IcreditAuthEntity authEntity : authList) {
            if(!request.getApiId().contains(authEntity.getApiId())) {
                cancelSelectedList.add(String.valueOf(new StringBuilder(authEntity.getApiId()).append(appEntity.getGenerateId())));
            }
        }
        redisTemplate.delete(cancelSelectedList);

        if(!CollectionUtils.isEmpty(authList)) {
            //删除旧的auth信息
            authConfigService.removeById(authList.get(0).getAuthConfigId());
            authMapper.removeByAppId(request.getAppId());
        }
        //保存auth信息
        for (String apiId : request.getApiId()) {
            IcreditAuthEntity authEntity = new IcreditAuthEntity();
            authEntity.setAppId(request.getAppId());
            authEntity.setApiId(apiId);
            authEntity.setAuthConfigId(authConfigEntity.getId());
            save(authEntity);
            String redisKey = String.valueOf(new StringBuilder(apiId).append(appEntity.getGenerateId()));
            Object appAuthAppObject = redisTemplate.opsForValue().get(redisKey);
            RedisAppAuthInfo appAuthInfo = null;
            if(Objects.isNull(appAuthAppObject)){
                appAuthInfo = new RedisAppAuthInfo(authConfigEntity.getPeriodBegin(), authConfigEntity.getPeriodEnd(), authConfigEntity.getAllowCall(), 0);
            }else{
                appAuthInfo = JSON.parseObject(appAuthAppObject.toString(), RedisAppAuthInfo.class);
                appAuthInfo = new RedisAppAuthInfo(authConfigEntity.getPeriodBegin(), authConfigEntity.getPeriodEnd(), authConfigEntity.getAllowCall(), appAuthInfo.getCalled());
            }
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(appAuthInfo));
        }
        return BusinessResult.success(true);
    }

    @Override
    public List<IcreditAuthEntity> findByAppId(String appId) {
        return authMapper.findByAppId(appId);
    }

    @Override
    public BusinessResult<AuthInfoResult> authInfo(AuthInfoRequest request) {
        IcreditAppEntity appEntity = appService.getById(request.getAppId());
        List<IcreditAuthEntity> authEntityList = authService.findByAppId(request.getAppId());
        if(CollectionUtils.isEmpty(authEntityList)){
            return BusinessResult.success(null);
        }
        List<String> apiIds = new ArrayList<>(authEntityList.size());
        AuthInfoResult authInfoResult = new AuthInfoResult();
        authInfoResult.setAppName(appEntity.getName());
        authInfoResult.setAppId(appEntity.getId());
        for (IcreditAuthEntity icreditAuthEntity : authEntityList) {
            apiIds.add(icreditAuthEntity.getApiId());
        }
        List<ApiInfoDTO> apiInfoList = workFlowService.findApiInfoByApiIds(apiIds);
        List<ApiCascadeInfoResult> apiCascadeInfoList = new ArrayList<>();
        String initWorkFlowId = apiInfoList.get(0).getWorkFlowId();
        apiCascadeInfoList.add(new ApiCascadeInfoResult(initWorkFlowId, apiInfoList.get(0).getWorkFlowName(), null, false));
        for (ApiInfoDTO apiInfo : apiInfoList) {//获取业务流程
            if(!initWorkFlowId.equals(apiInfo.getWorkFlowId())){
                apiCascadeInfoList.add(new ApiCascadeInfoResult(apiInfo.getWorkFlowId(), apiInfo.getWorkFlowName(), null, false));
                initWorkFlowId = apiInfo.getWorkFlowId();
            }
        }
        for (ApiCascadeInfoResult apiCascadeInfoResult : apiCascadeInfoList) {//获取api分组
            List<ApiGroupIdAAndNameResult> apiGroupIdAAndNameResultList = new ArrayList<>();
            List<String> apiGroupIdList = new ArrayList<>();
            for (ApiInfoDTO apiInfo : apiInfoList) {
                if(apiCascadeInfoResult.getId().equals(apiInfo.getWorkFlowId()) && !apiGroupIdList.contains(apiInfo.getApiGroupId())){
                    apiGroupIdAAndNameResultList.add(new ApiGroupIdAAndNameResult(apiInfo.getApiGroupId(), apiInfo.getApiGroupName(), null, false));
                    apiGroupIdList.add(apiInfo.getApiGroupId());
                }
            }
            apiCascadeInfoResult.setChildren(apiGroupIdAAndNameResultList);
        }
        for (ApiCascadeInfoResult apiCascadeInfoResult : apiCascadeInfoList) {//获取api
            for (ApiGroupIdAAndNameResult apiGroup : apiCascadeInfoResult.getChildren()) {
                List<ApiIdAAndNameResult> apiIdAAndNameResultList = new ArrayList<>();
                List<String> apiIdList = new ArrayList<>();
                for (ApiInfoDTO apiInfo : apiInfoList) {
                    if(apiCascadeInfoResult.getId().equals(apiInfo.getWorkFlowId()) && apiGroup.getId().equals(apiInfo.getApiGroupId()) && !apiIdList.contains(apiInfo.getApiId())){
                        apiIdAAndNameResultList.add(new ApiIdAAndNameResult(apiInfo.getApiId(), apiInfo.getApiName(), true));
                        apiIdList.add(apiInfo.getApiId());
                    }
                }
                apiGroup.setChildren(apiIdAAndNameResultList);
            }
        }
        authInfoResult.setApiCascadeInfoStrList(apiCascadeInfoList);

        AuthResult authResult = appService.generateAuthResultInfo(authEntityList.get(0).getAuthConfigId(), appEntity.getTokenType());
        authInfoResult.setAuthResult(authResult);
        return BusinessResult.success(authInfoResult);
    }
}
