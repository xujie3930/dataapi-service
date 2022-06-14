package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisAppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ApiInfoDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.AuthEffectiveTimeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.AuthInfoTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.RequestFiledEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthConfigMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.*;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import javafx.beans.binding.MapExpression;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.Charset;
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
    @Resource
    private IcreditAuthConfigMapper authConfigMapper;
    @Autowired
    private IcreditApiBaseService apiBaseService;


    @Override
    @Transactional
    public BusinessResult<ApiSaveResult> saveOuterApiDef(String userId, AuthSaveApiRequest request) {
        //保存时，先发布接口再授权
        //为了避免分布式事务，此处做到一块去
        DatasourceApiSaveParam param = new DatasourceApiSaveParam();
        BeanUtils.copyProperties(request.getApiSaveRequest(), param);
        BusinessResult<ApiSaveResult> andPublish = apiBaseService.createAndPublish(userId, param);
        if (!andPublish.isSuccess() || Objects.isNull(andPublish.getData())) {
            return andPublish;
        }
        String apiId = andPublish.getData().getId();//app id
        request.setApiId(apiId);
        //同时保存授权信息到redis
        //查找api信息
        IcreditApiBaseEntity apiBaseEntity = apiBaseMapper.selectById(request.getApiId());
        if(null==apiBaseEntity){
            ResourceCodeBean.ResourceCode rc20000054 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000054;
            throw new AppException(rc20000054.getCode(), rc20000054.getMessage());
        }
        /*if(null==apiBaseEntity.getInterfaceSource() || apiBaseEntity.getInterfaceSource()!=1){
            ResourceCodeBean.ResourceCode rc20000057 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000057;
            return BusinessResult.fail(rc20000057.getCode(), rc20000057.getMessage());
        }*/
        //查找应用信息
        Collection<IcreditAppEntity> appList = (request.getAppIds().isEmpty()?new ArrayList<>(0):appService.listByIds(request.getAppIds()));
        Map<String, IcreditAppEntity> appMap = (null==appList || appList.isEmpty())?new HashMap<>(0):appList.stream().collect(Collectors.toMap(IcreditAppEntity::getId, IcreditAppEntity->IcreditAppEntity));
        if(!request.getAppIds().isEmpty() && request.getAppIds().size()!=appMap.size()){
            ResourceCodeBean.ResourceCode rc20000010 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000010;
            throw new AppException(rc20000010.getCode(), rc20000010.getMessage());
        }
        //获取已授权列表
        final Map<String, Object> paramsMap = new HashMap<>(2);
        paramsMap.put("apiId", apiBaseEntity.getId());
        List<Map<String, Object>> authList = authMapper.findOuterAuthList(paramsMap);
        if(null!=authList & !authList.isEmpty()){
            //如果存在已授权列表
            //删除redis授权信息
            List<String> cancelSelectedList = new ArrayList<>();
            Set<String> authConfigIds = new HashSet<>();
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> auth : authList) {
                authConfigIds.add((String) auth.get("authConfigId"));
                sb.setLength(0);
                if(!request.getAppIds().contains(auth.get("appId"))){
                    //cancelSelectedList保存取消勾选
                    cancelSelectedList.add(sb.append(auth.get("apiId")).append(auth.get("appGenerateId")).toString());
                }
            }

            //删除旧的auth信息
            Map<String, Object> delauthMap = new HashMap<>(2);
            delauthMap.put("apiId", apiBaseEntity.getId());
            authMapper.deletes(delauthMap);
            //删除旧的config信息
            //先判断config是否存在其他引用
            //判断authConfig是否还有其他引用
            List<Map<String, Object>> authConfigNumList = authMapper.getAuthNumByConfigIds(authConfigIds);
            //获取没有被引用的config，并且删除
            if(null!=authConfigNumList && !authConfigNumList.isEmpty()){
                authConfigNumList.stream().forEach(authNumMap -> {
                    final String authConfigId = (String) authNumMap.get("authConfigId");
                    final Long authNum = (Long) authNumMap.get("authNum");
                    if(authNum.intValue()>0){
                        //该配置还有引用，不可删除
                        authConfigIds.remove(authConfigId);
                    }
                });
            }
            if(!authConfigIds.isEmpty()){
                //此时authConfigIds不存在其他引用
                Map<String, Object> delconfMap = new HashMap<>(2);
                delconfMap.put("ids", authConfigIds);
                authConfigMapper.deletes(delconfMap);//改为逻辑删除
            }

            if(!cancelSelectedList.isEmpty()){
                //cancelSelectedList不为空，表示有取消勾选的情况
                redisTemplate.delete(cancelSelectedList);
            }

        }

        if(!request.getAppIds().isEmpty()){
            //当数组为空时，表示取消所有的授权，因此此处不走
            IcreditAuthConfigEntity config = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());
            authConfigService.saveOrUpdate(config);

            //保存auth信息
            List<IcreditAuthEntity> saveDb = new ArrayList<>();
            Map<String, String> saveRedis = new HashMap<>();
            for (String appId : request.getAppIds()) {
                IcreditAuthEntity authEntity = new IcreditAuthEntity();
                authEntity.setAppId(appId);
                authEntity.setApiId(apiBaseEntity.getId());
                authEntity.setAuthConfigId(config.getId());
                saveDb.add(authEntity);
                //save(authEntity);
                String redisKey = String.valueOf(new StringBuilder(apiBaseEntity.getId()).append(appMap.get(appId).getGenerateId()));
                Object apiAuthApp = redisTemplate.opsForValue().get(redisKey);
                RedisAppAuthInfo appAuthInfo = new RedisAppAuthInfo(config.getPeriodBegin(), config.getPeriodEnd(), config.getAllowCall(), Objects.isNull(apiAuthApp)?0:JSON.parseObject(apiAuthApp.toString(), RedisAppAuthInfo.class).getCalled());
                saveRedis.put(redisKey, JSON.toJSONString(appAuthInfo));
                //redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(appAuthInfo));
            }
            if(null!=saveDb && !saveDb.isEmpty()){
                //循环单条插入改为批量插入
                authMapper.batchInsert(saveDb);
                saveRedis.entrySet().stream().forEach(next->{
                    redisTemplate.opsForValue().set(next.getKey(), next.getValue());
                });
                /*redisTemplate.executePipelined(new RedisCallback<String>() {
                    @Override
                    public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
                        //StringRedisConnection src = (StringRedisConnection)redisConnection;
                        saveRedis.entrySet().stream().forEach(next->{
                            redisConnection.set(next.getKey().getBytes(Charset.forName("UTF-8")), next.getValue().getBytes(Charset.forName("UTF-8")));
                        });
                        return null;
                    }
                });*/
            }
        }


        return andPublish;
    }

    @Override
    @Transactional
    public BusinessResult<Boolean> saveDefV2(String userId, AuthSaveRequest request) {
        if(AuthEffectiveTimeEnum.SORT_TIME.getDurationType().equals(request.getDurationType()) && request.getAllowCall() < 0){
            ResourceCodeBean.ResourceCode rc20000036 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000036;
            return BusinessResult.fail(rc20000036.getCode(), rc20000036.getMessage());
        }
        if (CollectionUtils.isEmpty(request.getApiId())){
            ResourceCodeBean.ResourceCode rc20000009 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009;
            return BusinessResult.fail(rc20000009.getCode(), rc20000009.getMessage());
        }
        //同时保存授权信息到redis
        IcreditAppEntity appEntity = appService.getById(request.getAppId());
        if(null==appEntity){
            ResourceCodeBean.ResourceCode rc20000010 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000010;
            return BusinessResult.fail(rc20000010.getCode(), rc20000010.getMessage());
        }
        IcreditAuthConfigEntity authConfigEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());

        //保存auth信息
        List<IcreditAuthEntity> saveDb = new ArrayList<>();
        Map<String, String> saveRedis = new HashMap<>();
        for (String apiId : request.getApiId()) {
            authConfigEntity.setId(null);
            authConfigService.save(authConfigEntity);
            IcreditAuthEntity authEntity = new IcreditAuthEntity();
            authEntity.setAppId(request.getAppId());
            authEntity.setApiId(apiId);
            authEntity.setAuthConfigId(authConfigEntity.getId());

            String redisKey = String.valueOf(new StringBuilder(apiId).append(appEntity.getGenerateId()));
            RedisAppAuthInfo appAuthInfo = new RedisAppAuthInfo(authConfigEntity.getPeriodBegin(), authConfigEntity.getPeriodEnd(), authConfigEntity.getAllowCall(), 0);

            saveDb.add(authEntity);
            saveRedis.put(redisKey, JSON.toJSONString(appAuthInfo));
        }

        if(!saveDb.isEmpty() && authMapper.batchInsert(saveDb)>0){
            saveRedis.entrySet().stream().forEach(next->{
                redisTemplate.opsForValue().set(next.getKey(), next.getValue());
            });
        }
        //新增api时，同时修改外部api关联的应用的所有配置
        this.updateOuterConfigByApis(request);
        return BusinessResult.success(true);
    }

    /**
     * 如果新增的api包含外部api，则同步修改所有外部API关联的所有app的配置
     * @author  maoc
     * @create  2022/6/10 17:16
     * @desc
     **/
    private void updateOuterConfigByApis(AuthSaveRequest request){
        Collection<IcreditApiBaseEntity> apis = apiBaseService.listByIds(request.getApiId());
        List<String> apiIds = (null==apis || apis.isEmpty()?null:apis.stream().filter((IcreditApiBaseEntity a) -> (null != a.getInterfaceSource() && a.getInterfaceSource() == 1)).map(a -> a.getId()).collect(Collectors.toList()));
        if(null==apiIds || apiIds.isEmpty()){
            return;
        }
        //有符合的外部接口
        //修改该接口绑定的所有应用ID
        final Map<String, Object> paramsMap = new HashMap<>(2);
        paramsMap.put("apiIds", apiIds);
        List<Map<String, Object>> outerAuthList = authMapper.findOuterAuthList(paramsMap);//得到api关联的所有app
        if(null==outerAuthList || outerAuthList.isEmpty()){
            //没有关联关系
            return;
        }
        IcreditAuthConfigEntity authConfigEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());

        //生成新的配置信息
        Map<String, String> saveRedis = new HashMap<>(), batchUpdateMap = new HashMap<>();
        outerAuthList.stream().forEach(outerAuth->{


            String appId = (String) outerAuth.get("appId");
            String apiId = (String) outerAuth.get("apiId");
            String id = (String) outerAuth.get("id");
            String appGenerateId = (String) outerAuth.get("appGenerateId");

            if(!request.getAppId().equals(appId) || !request.getApiId().contains(apiId)){
                //过滤自己
                authConfigEntity.setId(null);
                authConfigService.save(authConfigEntity);

                String redisKey = String.valueOf(new StringBuilder(apiId).append(appGenerateId));
                Object appAuthAppObject = redisTemplate.opsForValue().get(redisKey);
                RedisAppAuthInfo appAuthInfo = new RedisAppAuthInfo(authConfigEntity.getPeriodBegin(), authConfigEntity.getPeriodEnd(), authConfigEntity.getAllowCall(), Objects.isNull(appAuthAppObject)?0:JSON.parseObject(appAuthAppObject.toString(), RedisAppAuthInfo.class).getCalled());
                saveRedis.put(redisKey, JSON.toJSONString(appAuthInfo));
                batchUpdateMap.put(id, authConfigEntity.getId());
            }

        });

        if(!batchUpdateMap.isEmpty()){
            authMapper.batchUpdateConfigIdByIds(batchUpdateMap);
            saveRedis.entrySet().stream().forEach(next->{
                redisTemplate.opsForValue().set(next.getKey(), next.getValue());
            });
        }
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
    @Transactional
    public BusinessResult<Boolean> configDef(String userId, AuthSaveRequest request) {
        if (CollectionUtils.isEmpty(request.getApiId())){
            ResourceCodeBean.ResourceCode rc20000009 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009;
            return BusinessResult.fail(rc20000009.getCode(), rc20000009.getMessage());
        }
        if(AuthEffectiveTimeEnum.SORT_TIME.getDurationType().equals(request.getDurationType()) && request.getAllowCall() < 0){
            ResourceCodeBean.ResourceCode rc20000036 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000036;
            return BusinessResult.fail(rc20000036.getCode(), rc20000036.getMessage());
        }
        if (CollectionUtils.isEmpty(request.getApiId())){
            ResourceCodeBean.ResourceCode rc20000009 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009;
            return BusinessResult.fail(rc20000009.getCode(), rc20000009.getMessage());
        }
        //查询API是否全部为内部API
        Set<String> apiId = new HashSet<>(request.getApiId());
        if(apiId.size()!=apiBaseMapper.queryInnerApiCount(apiId).intValue()){
            ResourceCodeBean.ResourceCode rc20000058 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000058;
            return BusinessResult.fail(rc20000058.getCode(), rc20000058.getMessage());
        }

        IcreditAuthConfigEntity configEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());
        IcreditAppEntity appEntity = appService.getById(request.getAppId());
        List<IcreditAuthEntity> authList = authMapper.findByAppId(request.getAppId());
        if(null==authList || authList.isEmpty()){
            ResourceCodeBean.ResourceCode rc20000009 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009;
            return BusinessResult.fail(rc20000009.getCode(), rc20000009.getMessage());
        }

        //额外补充逻辑：由于历史数据原因（多对一，多个授权公用一个配置。新版本改为一对一，一个授权对应一个配置），兼容历史数据
        //判断：在配置时，如果关联的配置被多个授权引用，则新增配置。否则直接修改
        //获取所有对应的configId
        Set<String> configsIdSet = authList.stream().filter(e->request.getApiId().contains(e.getApiId())).map(IcreditAuthEntity::getAuthConfigId).collect(Collectors.toSet());
        //判断authConfig是否还有其他引用
        List<Map<String, Object>> authNumList = authMapper.getAuthNumByConfigIds(configsIdSet);
        //获取没有被引用的config，并且删除
        if(null!=authNumList && !authNumList.isEmpty()){
            authNumList.stream().forEach(authNumMap -> {
                final String authConfigId = (String) authNumMap.get("authConfigId");
                final Long authNum = (Long) authNumMap.get("authNum");
                if(authNum.intValue()<=1){
                    //只被一个授权引用的配置，移除
                    configsIdSet.remove(authConfigId);
                }
            });
        }

        Map<String, String> updateRedisMap = new HashMap<>(), updateAuthMap = new HashMap<>(8);
        Set<String> updateDbSet = new HashSet<>();
        for (IcreditAuthEntity authEntity : authList) {
            if(request.getApiId().contains(authEntity.getApiId())) {
                String redisKey = String.valueOf(new StringBuilder(authEntity.getApiId()).append(appEntity.getGenerateId()));
                Object appAuthAppObject = redisTemplate.opsForValue().get(redisKey);
                RedisAppAuthInfo appAuthInfo = new RedisAppAuthInfo(configEntity.getPeriodBegin(), configEntity.getPeriodEnd(), configEntity.getAllowCall(), Objects.isNull(appAuthAppObject)?0:JSON.parseObject(appAuthAppObject.toString(), RedisAppAuthInfo.class).getCalled());

                updateRedisMap.put(redisKey, JSON.toJSONString(appAuthInfo));

                if(configsIdSet.contains(authEntity.getAuthConfigId())){
                    //此配置被多个授权引用
                    IcreditAuthConfigEntity authConfigEntity = BeanCopyUtils.copyProperties(request, new IcreditAuthConfigEntity());
                    authConfigService.save(authConfigEntity);
                    updateAuthMap.put(authEntity.getId(), authConfigEntity.getId());
                }else{
                    updateDbSet.add(authEntity.getAuthConfigId());
                }

            }

            if(updateRedisMap.size() == request.getApiId().size()){
                break;
            }
        }
        //保存DB信息
        if(!updateRedisMap.isEmpty()){
            if(!updateDbSet.isEmpty() && authConfigService.updateByIds(configEntity, updateDbSet)<=0){
                ResourceCodeBean.ResourceCode rc60000003 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_60000003;
                throw new AppException(rc60000003.getCode(), rc60000003.getMessage());
            }
            if(!updateAuthMap.isEmpty() && authMapper.batchUpdateConfigIdByIds(updateAuthMap)<=0){
                ResourceCodeBean.ResourceCode rc60000003 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_60000003;
                throw new AppException(rc60000003.getCode(), rc60000003.getMessage());
            }
            //保存redis信息
            updateRedisMap.entrySet().stream().forEach(next->{
                redisTemplate.opsForValue().set(next.getKey(), next.getValue());
            });
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

        if(null!=authEntityList && !authEntityList.isEmpty() && !StringUtils.isEmpty(request.getApiId())){
            authEntityList = authEntityList.stream().filter(e -> request.getApiId().equals(e.getApiId())).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(authEntityList)){//新增授权
            List<ApiCascadeInfoResult> notSelectedApiCascadeInfoList = new ArrayList<>(0);
            if(StringUtils.isEmpty(request.getApiId())){
                //单个配置查询时，不返回该列表
                List<ApiInfoDTO> allApiInfoList = workFlowService.findApiInfoByApiIds(null, request.getPublishStatus());//所有业务流程、api分组、api
                notSelectedApiCascadeInfoList = (StringUtils.isEmpty(request.getApiId())?handleApiInfo(allApiInfoList):new ArrayList<>(0));
            }

            authInfoResult.setNoApiCascadeInfoStrList(notSelectedApiCascadeInfoList);
            authInfoResult.setInfoType(AuthInfoTypeEnum.ADD.getCode());
            return BusinessResult.success(authInfoResult);
        }



        //编辑授权

        authInfoResult.setAppName(appEntity.getName());
        authInfoResult.setAppId(appEntity.getId());

        //已授权的业务流程、api分组、api

//        //移除已选择的api
//        List<ApiInfoDTO> allNoSelectedApiInfoList = new ArrayList();
//        for (ApiInfoDTO apiInfoDTO : allApiInfoList) {
//            for (ApiInfoDTO infoDTO : apiInfoList) {
//                if(!infoDTO.equals(apiInfoDTO)){
//                    allNoSelectedApiInfoList.add(apiInfoDTO);
//                }
//            }
//        }
        List<ApiCascadeInfoResult> noApiCascadeInfoList = new ArrayList<>(0), apiCascadeInfoList = new ArrayList<>(0);
        if(StringUtils.isEmpty(request.getApiId())){
            //单个配置查询时，不返回该列表
            List<String> apiIds = new ArrayList<>(authEntityList.size());
            for (IcreditAuthEntity icreditAuthEntity : authEntityList) {
                apiIds.add(icreditAuthEntity.getApiId());
            }
            List<ApiInfoDTO> allApiInfoList = workFlowService.findApiInfoByApiIds(null, request.getPublishStatus());//所有业务流程、api分组、api
            List<ApiInfoDTO> apiInfoList = workFlowService.findApiInfoByApiIds(apiIds, request.getPublishStatus());
            noApiCascadeInfoList = handleApiInfo(allApiInfoList);
            apiCascadeInfoList = handleApiInfo(apiInfoList);
        }

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
    public BusinessPageResult<AuthListResult> authList(AuthListRequest request) {
        //查询数目
        Long count = authMapper.getCountByAppId(request);
        List<AuthListResult> list = ((null==count||count.intValue()<1)?new ArrayList<>(0):authMapper.getListByAppId(request));
        return BusinessPageResult.build(list, request, count);
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
    @Transactional
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
        if(delRedisList.isEmpty()){
            return BusinessResult.success(true);
        }

        //判断authConfig是否还有其他引用
        final Set<String> delConfigId = new HashSet<>();
        List<Map<String, Object>> authNumList = authMapper.getAuthNumByConfigIds(authConfigSet);
        //获取没有被引用的config，并且删除
        if(null!=authNumList && !authNumList.isEmpty()){
            authNumList.stream().forEach(authNumMap -> {
                final String authConfigId = (String) authNumMap.get("authConfigId");
                final Long authNum = (Long) authNumMap.get("authNum");
                authConfigSet.remove(authConfigId);
                if(authNum.intValue()<=1){
                    //该配置已经没有引用了，删除
                    delConfigId.add(authConfigId);
                }
            });
            if(!authConfigSet.isEmpty()){
                //authConfigSet里面的数据为没有被引用的配置
                delConfigId.addAll(authConfigSet);
            }
        }else{
            delConfigId.addAll(authConfigSet);
        }

        //删除
        final Map<String, Object> paramsMap = new HashMap<>(4);
        paramsMap.put("ids", request.getAuthList());
        paramsMap.put("appId", request.getAppId());
        if(authMapper.deletes(paramsMap)<=0){
            //没有授权关系
            return BusinessResult.success(true);
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
