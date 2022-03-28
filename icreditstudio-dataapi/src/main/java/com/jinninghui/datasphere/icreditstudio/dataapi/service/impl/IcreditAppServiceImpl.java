package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppEnableEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ApiInfoDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.AuthEffectiveTimeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.CallCountEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.TokenTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppDetailRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppEnableRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppUpdatePageInfoResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 应用 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditAppServiceImpl extends ServiceImpl<IcreditAppMapper, IcreditAppEntity> implements IcreditAppService {

    private static final String SPLIT_CHAR = "/";

    @Resource
    private IcreditAppMapper appMapper;
    @Resource
    private IcreditWorkFlowService workFlowService;
    @Resource
    private IcreditAppGroupService appGroupService;
    @Resource
    private IcreditAuthService authService;
    @Resource
    private IcreditAuthConfigService authConfigService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BusinessResult<String> saveDef(String userId, AppSaveRequest request) {
        StringLegalUtils.checkLegalNameForApp(request.getName());
        StringLegalUtils.checkLegalSecretContentForApp(request.getSecretContent());
        StringLegalUtils.checkLegalDescForApp(request.getDesc());
        StringLegalUtils.checkLegalAllowIpForApp(request.getAllowIp());
        IcreditAppEntity appEntity = BeanCopyUtils.copyProperties(request, new IcreditAppEntity());
        if(TokenTypeEnum.LONG_TIME.getCode().equals(request.getTokenType())){//token有效期--长期
            appEntity.setPeriod(TokenTypeEnum.LONG_TIME.getDuration());
        }if(TokenTypeEnum.EIGHT_HOURS.getCode().equals(request.getTokenType())){//token有效期--8小时
            appEntity.setPeriod(TokenTypeEnum.EIGHT_HOURS.getDuration());
        }
        saveOrUpdate(appEntity);
        if(AppEnableEnum.ENABLE.getCode().equals(request.getIsEnable())) {//启用状态才保存到redis
            AppAuthInfo appAuthInfo = BeanCopyUtils.copyProperties(appEntity, new AppAuthInfo());
            //新增应用时候，保存应用信息至redis
            redisTemplate.opsForValue().set(request.getGenerateId(), JSON.toJSONString(appAuthInfo));
        }
        return BusinessResult.success(appEntity.getId());
    }

    @Override
    public BusinessResult<Boolean> enableOrStop(AppEnableRequest request) {
        IcreditAppEntity appEntity = getById(request.getId());
        appEntity.setIsEnable(request.getIsEnable());
        boolean update = updateById(appEntity);
        AppAuthInfo appAuthInfo = BeanCopyUtils.copyProperties(appEntity, new AppAuthInfo());
        //启用时，保存应用信息至redis
        if(AppEnableEnum.ENABLE.getCode().equals(request.getIsEnable())) {
            redisTemplate.opsForValue().set(appEntity.getGenerateId(), JSON.toJSONString(appAuthInfo));
        }else{
            redisTemplate.delete(appEntity.getGenerateId());
        }
        return BusinessResult.success(update);
    }

    @Override
    public Boolean hasExitByGenerateId(String generateId, String appGroupId) {
        return appMapper.hasExitByGenerateId(generateId, appGroupId);
    }

    @Override
    public BusinessResult<AppDetailResult> detail(AppDetailRequest request) {
        StringLegalUtils.checkId(request.getId());
        IcreditAppEntity appEntity = appMapper.selectById(request.getId());
        AppDetailResult appDetailResult = new AppDetailResult();
        BeanUtils.copyProperties(appEntity, appDetailResult);
        if(TokenTypeEnum.LONG_TIME.getCode().equals(appEntity.getTokenType())){
            appDetailResult.setPeriod(TokenTypeEnum.LONG_TIME.getMsg());
        }else if(TokenTypeEnum.EIGHT_HOURS.getCode().equals(appEntity.getTokenType())){
            appDetailResult.setPeriod(TokenTypeEnum.EIGHT_HOURS.getMsg());
        }else{
            appDetailResult.setPeriod(String.valueOf(new StringBuilder().append(appEntity.getPeriod()).append("小时")));
        }
        appDetailResult.setCreateTime(appEntity.getCreateTime().getTime());
        appDetailResult.setAppGroupName(appGroupService.findNameById(appEntity.getAppGroupId()));

        List<IcreditAuthEntity> authEntityList = authService.findByAppId(appEntity.getId());
        if(!CollectionUtils.isEmpty(authEntityList)){//授权信息
            List<String> apiIds = new ArrayList<>(authEntityList.size());
            for (IcreditAuthEntity icreditAuthEntity : authEntityList) {
                apiIds.add(icreditAuthEntity.getApiId());
            }
            List<ApiInfoDTO> apiInfoList = workFlowService.findApiInfoByApiIds(apiIds);
            StringBuffer apiInfoStr = new StringBuffer();
            int len = apiInfoList.size();
            for (int i = 0;i < len;i++) {
                apiInfoStr.append(apiInfoList.get(i).getWorkFlowName()).append(SPLIT_CHAR).append(apiInfoList.get(i).getApiGroupName()).append(SPLIT_CHAR).append(apiInfoList.get(i).getApiName());
                if(i < (len - 1)){
                    apiInfoStr.append("、");
                }
            }
            ApiResult apiResult = new ApiResult();
            apiResult.setApiNames(String.valueOf(apiInfoStr));

            AuthResult authResult = generateAuthResultInfo(authEntityList.get(0).getAuthConfigId(), appEntity.getTokenType());

            appDetailResult.setApiResult(apiResult);
            appDetailResult.setAuthResult(authResult);
        }
        return BusinessResult.success(appDetailResult);
    }

    @Override
    public AuthResult generateAuthResultInfo(String authConfigId, Integer tokenType){
        AuthResult authResult = new AuthResult();
        IcreditAuthConfigEntity authConfigEntity = authConfigService.getById(authConfigId);
        authResult.setAllowCall(authConfigEntity.getAllowCall());
        authResult.setPeriodBegin(authConfigEntity.getPeriodBegin());
        authResult.setPeriodEnd(authConfigEntity.getPeriodEnd());
        authResult.setTokenType(tokenType);
        if(CallCountEnum.CALL_INFINITE_TIMES.getCode().equals(authConfigEntity.getAllowCall())){
            authResult.setCallCountType(CallCountEnum.CALL_INFINITE_TIMES.getCallTime());
        }else {
            authResult.setCallCountType(CallCountEnum.CALL_FINITE_TIMES.getCallTime());
        }
        if(AuthEffectiveTimeEnum.LONG_TIME.getCode().equals(authConfigEntity.getPeriodBegin())){
            authResult.setAuthEffectiveTime(AuthEffectiveTimeEnum.LONG_TIME.getEffective());
        }else {
            authResult.setAuthEffectiveTime(AuthEffectiveTimeEnum.SORT_TIME.getEffective());
        }
        return authResult;
    }

    @Override
    public String findEnableAppIdByAppGroupIds(List<String> appGroupIds) {
        return appMapper.findEnableAppIdByAppGroupIds(appGroupIds);
    }

    @Override
    public List<String> getIdsByAppGroupIds(List<String> ids) {
        return appMapper.getIdsByAppGroupIds(ids);
    }

    @Override
    public String findEnableAppIdByIds(List<String> appIds) {
        return appMapper.findEnableAppIdByIds(appIds);
    }

    @Override
    public BusinessResult<AppUpdatePageInfoResult> updatePageInfo(AppDetailRequest request) {
        StringLegalUtils.checkId(request.getId());
        IcreditAppEntity appEntity = appMapper.selectById(request.getId());
        AppUpdatePageInfoResult updatePageInfoResult = new AppUpdatePageInfoResult();
        BeanUtils.copyProperties(appEntity, updatePageInfoResult);
        if(TokenTypeEnum.CUSTOM.getCode().equals(appEntity.getTokenType())){
            updatePageInfoResult.setPeriod(String.valueOf(new StringBuilder().append(appEntity.getPeriod()).append("小时")));
        }else {
            updatePageInfoResult.setPeriod(null);
        }
        updatePageInfoResult.setAppGroupName(appGroupService.findNameById(appEntity.getAppGroupId()));
        return BusinessResult.success(updatePageInfoResult);
    }
}
