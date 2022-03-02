package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ApiInfoDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
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
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
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
        IcreditAppEntity appEntity = BeanCopyUtils.copyProperties(request, new IcreditAppEntity());
        appEntity.setGenerateId(request.getGenerateId());
        save(appEntity);
        AppAuthInfo appAuthInfo = BeanCopyUtils.copyProperties(appEntity, new AppAuthInfo());
        //新增应用时候，保存应用信息至redis
        redisTemplate.opsForValue().set(request.getGenerateId(), JSON.toJSONString(appAuthInfo));
        return BusinessResult.success(appEntity.getId());
    }

    @Override
    public BusinessResult<Boolean> enableById(String userId, AppEnableRequest request) {
        IcreditAppEntity appEntity = getById(request.getId());
        appEntity.setIsEnable(request.getIsEnable());
        boolean update = updateById(appEntity);
        return BusinessResult.success(update);
    }

    @Override
    public Boolean hasExitByGenerateId(String generateId, String appGroupId) {
        return appMapper.hasExitByGenerateId(generateId, appGroupId);
    }

    @Override
    @ResultReturning
    public BusinessResult<AppDetailResult> detail(AppDetailRequest request) {
        IcreditAppEntity appEntity = appMapper.selectById(request.getId());
        AppDetailResult appDetailResult = new AppDetailResult();
        BeanUtils.copyProperties(appEntity, appDetailResult);
        appDetailResult.setSecretContent("******");
        appDetailResult.setCreateTime(appEntity.getCreateTime().getTime());
        appDetailResult.setAppGroupName(appGroupService.findNameById(appEntity.getAppGroupId()));

        List<IcreditAuthEntity> authEntityList = authService.findByAppId(appEntity.getId());
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

        IcreditAuthConfigEntity authConfigEntity = authConfigService.getById(authEntityList.get(0).getAuthConfigId());
        AuthResult authResult = new AuthResult();
        authResult.setAllowCall(authConfigEntity.getAllowCall());
        authResult.setPeriodBegin(authConfigEntity.getPeriodBegin());
        authResult.setPeriodEnd(authConfigEntity.getPeriodEnd());
        if(CallCountEnum.CALL_INFINITE_TIMES.getCode().equals(authConfigEntity.getAllowCall())){
            authResult.setCallCountType(CallCountEnum.CALL_INFINITE_TIMES.getMsg());
        }else {
            authResult.setCallCountType(CallCountEnum.CALL_FINITE_TIMES.getMsg());
        }
        if(TokenTypeEnum.LONG_TIME.getPeriod().equals(appDetailResult.getPeriod())) {
            authResult.setTokenType(TokenTypeEnum.LONG_TIME.getCode());
        }else if(TokenTypeEnum.EIGHT_HOURS.getPeriod().equals(appDetailResult.getPeriod())){
            authResult.setTokenType(TokenTypeEnum.EIGHT_HOURS.getCode());
        }else{
            authResult.setTokenType(TokenTypeEnum.CUSTOM.getCode());
        }

        appDetailResult.setApiResult(apiResult);
        appDetailResult.setAuthResult(authResult);
        return BusinessResult.success(appDetailResult);
    }
}
