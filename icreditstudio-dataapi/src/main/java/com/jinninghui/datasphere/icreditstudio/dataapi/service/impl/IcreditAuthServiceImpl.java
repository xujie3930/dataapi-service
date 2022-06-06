package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisAppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ApiInfoDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.AuthEffectiveTimeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.AuthInfoTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthConfigService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.*;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    private IcreditApiBaseMapper apiBaseMapper;

    @Override
    public BusinessResult<Boolean> saveApiDef(String userId, AuthSaveApiRequest request) {
        if(AuthEffectiveTimeEnum.SORT_TIME.getDurationType().equals(request.getDurationType()) && request.getAllowCall() < 0){
            ResourceCodeBean.ResourceCode resourceCode20000036 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000036;
            return BusinessResult.fail(resourceCode20000036.getCode(), resourceCode20000036.getMessage());
        }

        if(StringUtils.isEmpty(request.getApiId())){
            ResourceCodeBean.ResourceCode resourceCode20000009 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009;
            return BusinessResult.fail(resourceCode20000009.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
        }
        if (CollectionUtils.isEmpty(request.getAppIds())){
            ResourceCodeBean.ResourceCode resourceCode20000021 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000021;
            throw new AppException(resourceCode20000021.getCode(), resourceCode20000021.getMessage());
        }
        IcreditAuthConfigEntity authConfigEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());
        authConfigService.saveOrUpdate(authConfigEntity);
        //同时保存授权信息到redis
        //查找应用信息
        Collection<IcreditAppEntity> appList = appService.listByIds(request.getAppIds());
        Map<String, IcreditAppEntity> appMap = (null==appList || appList.isEmpty())?new HashMap<>(0):appList.stream().collect(Collectors.toMap(IcreditAppEntity::getId, IcreditAppEntity->IcreditAppEntity));
        //获取已授权列表
        List<IcreditAuthEntity> authList = authMapper.findByApiId(request.getApiId());
        //全部删除redis授权信息
        List<String> cancelSelectedList = new ArrayList<>();
        Set<String> authConfigIds = new HashSet<>();
        for (IcreditAuthEntity auth : authList) {
            authConfigIds.add(auth.getAuthConfigId());
            if(null!=appMap.get(auth.getAppId()) && !request.getAppIds().contains(auth.getAppId())){
                cancelSelectedList.add(String.valueOf(new StringBuilder(auth.getApiId()).append(appMap.get(auth.getAppId()).getGenerateId())));
            }

        }
        redisTemplate.delete(cancelSelectedList);

        if(!CollectionUtils.isEmpty(authConfigIds)) {
            //删除旧的auth信息
            authConfigService.removeByIds(authConfigIds);
            authMapper.removeByApiId(request.getApiId());
        }
        //保存auth信息
        List<IcreditAuthEntity> saveDb = new ArrayList<>();
        Map<String, String> saveRedis = new HashMap<>();
        for (String appId : request.getAppIds()) {
            IcreditAuthEntity authEntity = new IcreditAuthEntity();
            authEntity.setAppId(appId);
            authEntity.setApiId(request.getApiId());
            authEntity.setAuthConfigId(authConfigEntity.getId());
            saveDb.add(authEntity);
            //save(authEntity);
            String redisKey = String.valueOf(new StringBuilder(request.getApiId()).append(appMap.get(appId).getGenerateId()));
            Object appAuthAppObject = redisTemplate.opsForValue().get(redisKey);
            RedisAppAuthInfo appAuthInfo = new RedisAppAuthInfo(authConfigEntity.getPeriodBegin(), authConfigEntity.getPeriodEnd(), authConfigEntity.getAllowCall(), Objects.isNull(appAuthAppObject)?0:JSON.parseObject(appAuthAppObject.toString(), RedisAppAuthInfo.class).getCalled());
            /*if(Objects.isNull(appAuthAppObject)){
                appAuthInfo = new RedisAppAuthInfo(authConfigEntity.getPeriodBegin(), authConfigEntity.getPeriodEnd(), authConfigEntity.getAllowCall(), 0);
            }else{
                appAuthInfo = JSON.parseObject(appAuthAppObject.toString(), RedisAppAuthInfo.class);
                appAuthInfo = new RedisAppAuthInfo(authConfigEntity.getPeriodBegin(), authConfigEntity.getPeriodEnd(), authConfigEntity.getAllowCall(), appAuthInfo.getCalled());
            }*/
            saveRedis.put(redisKey, JSON.toJSONString(appAuthInfo));
        }
        if(null!=saveDb && !saveDb.isEmpty()){
            authMapper.batchInsert(saveDb);
            redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    StringRedisConnection src = (StringRedisConnection)redisConnection;
                    saveRedis.entrySet().stream().forEach(next->{
                        src.set(next.getKey(), next.getValue());
                    });
                    return null;
                }
            });
        }
        return BusinessResult.success(true);
    }

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
        AuthInfoResult authInfoResult = new AuthInfoResult();
        List<ApiInfoDTO> allApiInfoList = workFlowService.findApiInfoByApiIds(null);//所有业务流程、api分组、api
        if(CollectionUtils.isEmpty(authEntityList)){//新增授权
            List<ApiCascadeInfoResult> notSelectedApiCascadeInfoList = handleApiInfo(allApiInfoList);
            authInfoResult.setNoApiCascadeInfoStrList(notSelectedApiCascadeInfoList);
            authInfoResult.setInfoType(AuthInfoTypeEnum.ADD.getCode());
            return BusinessResult.success(authInfoResult);
        }

        //编辑授权
        List<String> apiIds = new ArrayList<>(authEntityList.size());
        authInfoResult.setAppName(appEntity.getName());
        authInfoResult.setAppId(appEntity.getId());
        for (IcreditAuthEntity icreditAuthEntity : authEntityList) {
            apiIds.add(icreditAuthEntity.getApiId());
        }
        List<ApiInfoDTO> apiInfoList = workFlowService.findApiInfoByApiIds(apiIds);//已授权的业务流程、api分组、api

//        //移除已选择的api
//        List<ApiInfoDTO> allNoSelectedApiInfoList = new ArrayList();
//        for (ApiInfoDTO apiInfoDTO : allApiInfoList) {
//            for (ApiInfoDTO infoDTO : apiInfoList) {
//                if(!infoDTO.equals(apiInfoDTO)){
//                    allNoSelectedApiInfoList.add(apiInfoDTO);
//                }
//            }
//        }

        List<ApiCascadeInfoResult> noApiCascadeInfoList = handleApiInfo(allApiInfoList);
        List<ApiCascadeInfoResult> apiCascadeInfoList = handleApiInfo(apiInfoList);
        authInfoResult.setApiCascadeInfoStrList(apiCascadeInfoList);
        authInfoResult.setNoApiCascadeInfoStrList(noApiCascadeInfoList);

        AuthResult authResult = appService.generateAuthResultInfo(authEntityList.get(0).getAuthConfigId(), appEntity.getTokenType());
        authInfoResult.setAuthResult(authResult);
        authInfoResult.setInfoType(AuthInfoTypeEnum.UPDATE.getCode());
        return BusinessResult.success(authInfoResult);
    }

    private List<ApiCascadeInfoResult> handleApiInfo(List<ApiInfoDTO> apiInfoList){
        List<ApiCascadeInfoResult> apiCascadeInfoList = new ArrayList<>();
        List<String> workFlowIdList = new ArrayList<>();
        for (ApiInfoDTO apiInfo : apiInfoList) {//获取业务流程
            if(!workFlowIdList.contains(apiInfo.getWorkFlowId())){
                apiCascadeInfoList.add(new ApiCascadeInfoResult(apiInfo.getWorkFlowId(), apiInfo.getWorkFlowName(), null, false, 1));
                workFlowIdList.add(apiInfo.getWorkFlowId());
            }
        }
        for (ApiCascadeInfoResult apiCascadeInfoResult : apiCascadeInfoList) {//获取api分组
            List<ApiGroupIdAAndNameResult> apiGroupIdAAndNameResultList = new ArrayList<>();
            List<String> apiGroupIdList = new ArrayList<>();
            for (ApiInfoDTO apiInfo : apiInfoList) {
                if(!apiGroupIdList.contains(apiInfo.getApiGroupId()) && apiCascadeInfoResult.getId().equals(apiInfo.getWorkFlowId())){
                    apiGroupIdAAndNameResultList.add(new ApiGroupIdAAndNameResult(apiInfo.getApiGroupId(), apiInfo.getApiGroupName(), null, false, 2, apiInfo.getWorkFlowId()));
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
                    if(!apiIdList.contains(apiInfo.getApiId()) && apiCascadeInfoResult.getId().equals(apiInfo.getWorkFlowId()) && apiGroup.getId().equals(apiInfo.getApiGroupId())){
                        apiIdAAndNameResultList.add(new ApiIdAAndNameResult(apiInfo.getApiId(), apiInfo.getApiName(), true, 3, apiInfo.getApiGroupId(), apiInfo.getWorkFlowId()));
                        apiIdList.add(apiInfo.getApiId());
                    }
                }
                apiGroup.setChildren(apiIdAAndNameResultList);
            }
        }
        return apiCascadeInfoList;
    }

    @Override
    public List<AuthListResult> authList(AuthListRequest request) {
        return authMapper.getListByAppId(request);
    }

    @Override
    public List<AuthListResult> queryApiAuthListByPath(String path) {
        IcreditApiBaseEntity apiEntity = apiBaseMapper.findByApiPath(path);
        if(null==apiEntity || StringUtils.isEmpty(apiEntity.getId())){
            return new ArrayList<>(0);
        }
        AuthListRequest request = new AuthListRequest();
        request.setApiId(apiEntity.getId());
        return authMapper.getApiAuthList(request);
    }

    @Override
    public BusinessResult<Boolean> del(String userId, AuthDelRequest request) {
        //同时保存授权信息到redis
        IcreditAppEntity appEntity = appService.getById(request.getAppId());
        if(null==appEntity){
            ResourceCodeBean.ResourceCode resourceCode20000010 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000010;
            return BusinessResult.fail(resourceCode20000010.getCode(), resourceCode20000010.getMessage());
        }
        List<IcreditAuthEntity> authList = authMapper.findByAppId(request.getAppId());
        if(null==authList || authList.isEmpty()){
            //没有授权，返回成功
            return BusinessResult.success(true);
        }
        StringBuilder sb = new StringBuilder();
        Set<String> authConfigSet = new HashSet<>(), delRedisList = new HashSet<>();
        authList.stream().forEach(auth->{
            if(request.getAuthList().contains(auth.getId())){
                sb.setLength(0);
                delRedisList.add(sb.append(auth.getApiId()).append(appEntity.getGenerateId()).toString());
                authConfigSet.add(auth.getAuthConfigId());
            }
        });
        //删除
        final Set<String> delConfigId = new HashSet<>();
        final Map<String, Object> paramsMap = new HashMap<>(4);
        paramsMap.put("ids", request.getAuthList());
        paramsMap.put("appId", request.getAppId());
        if(authMapper.deletes(paramsMap)<=0){
            //没有授权关系
            return BusinessResult.success(true);
        }
        //判断authConfig是否还有其他引用
        List<Map<String, Object>> authNumList = authMapper.getAuthNumByConfigIds(authConfigSet);
        //获取没有被引用的config，并且删除
        if(null!=authNumList && !authNumList.isEmpty()){
            authNumList.stream().forEach(authNumMap -> {
                final String authConfigId = (String) authNumMap.get("authConfigId");
                final Long authNum = (Long) authNumMap.get("authNum");
                if(authNum.intValue()<=0){
                    //该配置已经没有引用了，删除
                    delConfigId.add(authConfigId);
                }
            });
        }else{
            delConfigId.addAll(authConfigSet);
        }

        if(!delConfigId.isEmpty()){
            //删除没引用的config
            authConfigService.deleteByIds(delConfigId);
        }
        //删除缓存
        redisTemplate.delete(delRedisList);
        return BusinessResult.success(true);
    }
}
