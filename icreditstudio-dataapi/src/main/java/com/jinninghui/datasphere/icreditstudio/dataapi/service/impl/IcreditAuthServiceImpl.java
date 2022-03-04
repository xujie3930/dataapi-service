package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisAppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ApiInfoDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
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
import java.util.LinkedList;
import java.util.List;

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
        if (CollectionUtils.isEmpty(request.getApiId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
        }
        IcreditAuthConfigEntity authConfigEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());
        authConfigService.saveOrUpdate(authConfigEntity);
        //同时保存授权信息到redis
        IcreditAppEntity appEntity = appService.getById(request.getAppId());

        List<IcreditAuthEntity> authList = authMapper.findByAppId(request.getAppId());
        if(!CollectionUtils.isEmpty(authList)) {
            //删除旧的auth信息
            authConfigService.removeById(authList.get(0).getAuthConfigId());
            authMapper.removeByAppId(request.getAppId());
            //清除redis中的旧数据
            List<String> authInfoKeyList = new ArrayList<>();
            for (IcreditAuthEntity icreditAuthEntity : authList) {
                authInfoKeyList.add(String.valueOf(new StringBuilder(icreditAuthEntity.getApiId()).append(appEntity.getGenerateId())));
            }
            redisTemplate.delete(authInfoKeyList);
        }
        //保存auth信息
        for (String apiId : request.getApiId()) {
            IcreditAuthEntity authEntity = new IcreditAuthEntity();
            authEntity.setAppId(request.getAppId());
            authEntity.setApiId(apiId);
            authEntity.setAuthConfigId(authConfigEntity.getId());
            save(authEntity);
            RedisAppAuthInfo appAuthInfo = new RedisAppAuthInfo(authConfigEntity.getPeriodBegin(), authConfigEntity.getPeriodEnd(), authConfigEntity.getAllowCall());
            redisTemplate.opsForValue().set(apiId + appEntity.getGenerateId(), JSON.toJSONString(appAuthInfo));
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
        List<List<ApiCascadeInfoResult>> apiCascadeInfoStrList = new ArrayList<>();
        int len = apiInfoList.size();
        for (int i = 0;i < len;i++) {
            List<ApiCascadeInfoResult> apiCascadeList = new LinkedList<>();
            ApiCascadeInfoResult apiCascadeInfoResult = new ApiCascadeInfoResult();
            apiCascadeInfoResult.setId(apiInfoList.get(i).getWorkFlowId());
            apiCascadeInfoResult.setName(apiInfoList.get(i).getWorkFlowName());
            apiCascadeList.add(apiCascadeInfoResult);

            apiCascadeInfoResult = new ApiCascadeInfoResult();
            apiCascadeInfoResult.setId(apiInfoList.get(i).getApiGroupId());
            apiCascadeInfoResult.setName(apiInfoList.get(i).getApiGroupName());
            apiCascadeList.add(apiCascadeInfoResult);

            apiCascadeInfoResult = new ApiCascadeInfoResult();
            apiCascadeInfoResult.setId(apiInfoList.get(i).getApiId());
            apiCascadeInfoResult.setName(apiInfoList.get(i).getApiName());
            apiCascadeList.add(apiCascadeInfoResult);
            apiCascadeInfoStrList.add(apiCascadeList);
        }
        authInfoResult.setApiCascadeInfoStrList(apiCascadeInfoStrList);

        AuthResult authResult = appService.generateAuthResultInfo(authEntityList.get(0).getAuthConfigId(), appEntity.getTokenType());
        authInfoResult.setAuthResult(authResult);
        return BusinessResult.success(authInfoResult);
    }
}
