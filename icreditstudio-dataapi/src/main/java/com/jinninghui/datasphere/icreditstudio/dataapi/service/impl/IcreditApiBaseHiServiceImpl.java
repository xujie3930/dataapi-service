package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ApiParamInfoDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.DatasourceApiDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.RegisterApiDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.DataAssertFeignClient;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseHiMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.factory.ApiBaseFactory;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.*;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * API?????????????????????????????? ???????????????
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Slf4j
@Service
public class IcreditApiBaseHiServiceImpl extends ServiceImpl<IcreditApiBaseHiMapper, IcreditApiBaseHiEntity> implements IcreditApiBaseHiService {
    @Value("${system.prop.api.used.count.redis.key}")
    private String apiUsedCount;
    private static final String REDIS_KEY_SPLIT_JOINT_CHAR = ":";
    private static final String COMMA = ",";
    @Resource
    private DataAssertFeignClient dataAssertFeignClient;
    @Resource
    private IcreditApiBaseHiMapper apiBaseHiMapper;
    @Resource
    private IcreditRegisterApiService registerApiService;
    @Resource
    private IcreditApiBaseService apiBaseService;
    @Resource
    private ApiBaseFactory apiBaseFactory;
    @Resource
    private IcreditApiParamService apiParamService;
    @Resource
    private IcreditApiGroupService apiGroupService;
    @Resource
    private IcreditWorkFlowService workFlowService;
    @Value("${host.addr}")
    private String host;
    @Autowired
    private IcreditGenerateApiService generateApiService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public IcreditApiBaseHiEntity findByApiBaseId(String apiId) {
        return apiBaseHiMapper.findByApiBaseId(apiId);
    }

    @Override
    @ResultReturning
    public BusinessResult<BusinessPageResult<ApiHistoryListResult>> getList(ApiHistoryListRequest request) {
        StringLegalUtils.checkId(request.getApiId());
        if(!StringUtils.isEmpty(request.getPublishDateStart())) {
            request.setPublishDateStart(String.valueOf(new StringBuffer(request.getPublishDateStart()).append(" 00:00:00")));
        }
        if(!StringUtils.isEmpty(request.getPublishDateEnd())) {
            request.setPublishDateEnd(String.valueOf(new StringBuffer(request.getPublishDateEnd()).append(" 59:59:59")));
        }
        request.setPageStartNum((request.getPageNum() - 1) * request.getPageSize());
        List<ApiHistoryListResult> apiHistoryListResultList = apiBaseHiMapper.getList(request);
        Long apiBaseHiCount = apiBaseHiMapper.countApiBaseHi(request);
        return BusinessResult.success(BusinessPageResult.build(apiHistoryListResultList, request, apiBaseHiCount));
    }

    @Override
    @ResultReturning
    public BusinessResult<ApiDetailResult> info(ApiBaseHiDetailRequest request) {
        StringLegalUtils.checkId(request.getApiHiId());
        IcreditApiBaseHiEntity apiBaseHiEntity = apiBaseHiMapper.selectById(request.getApiHiId());

        ApiDetailResult result = new ApiDetailResult();
        if (Objects.isNull(apiBaseHiEntity)) {
            return BusinessResult.success(result);
        }
        BeanCopyUtils.copyProperties(apiBaseHiEntity, result);
        result.setApiPath(apiBaseHiEntity.getPath());
        result.setApiHiId(apiBaseHiEntity.getId());
        result.setId(apiBaseHiEntity.getApiBaseId());
        //????????????API????????????????????????
        ApiBaseService apiService = apiBaseFactory.getApiService(apiBaseHiEntity.getType());
        apiService.setApiBaseResult(result);
        //????????????????????????????????????
        IcreditApiGroupEntity apiGroupEntity = apiGroupService.getById(apiBaseHiEntity.getApiGroupId());
        if (!Objects.isNull(apiGroupEntity)) {
            result.setApiGroupName(apiGroupEntity.getName());
            result.setApiGroupId(apiGroupEntity.getId());
            IcreditWorkFlowEntity workFlowEntity = workFlowService.getById(apiGroupEntity.getWorkId());
            result.setWorkFlowName(workFlowEntity.getName());
            result.setWorkFlowId(workFlowEntity.getId());
            result.setDestination(String.valueOf(new StringBuilder(workFlowEntity.getName()).append("/").append(apiGroupEntity.getName())));
        }
        //?????????param??????
        List<IcreditApiParamEntity> apiParamEntityList = apiParamService.getByApiIdAndVersion(apiBaseHiEntity.getApiBaseId(), apiBaseHiEntity.getApiVersion());
        List<APIParamResult> apiParamList = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.copy(apiParamEntityList, APIParamResult.class);
        List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList = new ArrayList<>();
        List<RegisterResponseParamSaveRequest> registerResponseParamSaveRequestList = new ArrayList<>();
        List<APIParamResult> params = apiParamList.stream()
                .filter((APIParamResult a) -> RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(a.getIsRequest()))
                .collect(Collectors.toList());
        String address = getDatasourceInterfaceAddress(apiBaseHiEntity, params);
        if(ApiTypeEnum.API_GENERATE.getCode().equals(apiBaseHiEntity.getType())) {

        }else{
            handleRegisterApiParamInfo(registerRequestParamSaveRequestList, registerResponseParamSaveRequestList, apiParamEntityList);
            IcreditRegisterApiEntity registerApiEntity = registerApiService.findByApiIdAndApiVersion(apiBaseHiEntity.getApiBaseId(), apiBaseHiEntity.getApiVersion());
            result.setReqHost(registerApiEntity.getHost());
            result.setReqPath(registerApiEntity.getPath());
        }
        if(null!=result.getGenerateApi() && !StringUtils.isEmpty(result.getGenerateApi().getTableName())){
            BusinessResult<String> chineseNameResult = dataAssertFeignClient.getChineseName(result.getGenerateApi().getTableName());
            result.getGenerateApi().setTableChineseName(null==chineseNameResult?null:chineseNameResult.getData());
        }
        result.setParamList(apiParamList);
        result.setRegisterRequestParamSaveRequestList(registerRequestParamSaveRequestList);
        result.setRegisterResponseParamSaveRequestList(registerResponseParamSaveRequestList);
        result.setParamList(apiParamList);
        result.setInterfaceAddress(address);
        result.setProtocol("HTTP");
        result.setCreateTime(Optional.ofNullable(apiBaseHiEntity.getCreateTime()).orElse(new Date()).getTime());
        result.setPublishTime(Optional.ofNullable(apiBaseHiEntity.getPublishTime()).orElse(new Date()).getTime());
        return BusinessResult.success(result);
    }

    private void handleRegisterApiParamInfo(List<RegisterRequestParamSaveRequest> registerRequestList, List<RegisterResponseParamSaveRequest> registerResponseList, List<IcreditApiParamEntity> apiParamList) {
        for (IcreditApiParamEntity apiParamEntity : apiParamList) {
            if(null == apiParamEntity.getIsRequest()){//????????????
                RegisterResponseParamSaveRequest registerResponseParam = new RegisterResponseParamSaveRequest();
                BeanUtils.copyProperties(apiParamEntity, registerResponseParam);
                registerResponseList.add(registerResponseParam);
            }
            if(null == apiParamEntity.getIsResponse()){//????????????
                RegisterRequestParamSaveRequest registerRequestParam = new RegisterRequestParamSaveRequest();
                BeanUtils.copyProperties(apiParamEntity, registerRequestParam);
                registerRequestList.add(registerRequestParam);
            }
        }
    }

    private String getRegisterInterfaceAddress(IcreditApiBaseHiEntity apiBaseHiEntity, List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList){
        IcreditRegisterApiEntity registerApiEntity = registerApiService.findByApiIdAndApiVersion(apiBaseHiEntity.getApiBaseId(), apiBaseHiEntity.getApiVersion());
        StringBuilder address = new StringBuilder(registerApiEntity.getHost());
        if(registerApiEntity.getPath().startsWith("/")){
            address.append(registerApiEntity.getPath());
        }else{
            address.append("/").append(registerApiEntity.getPath());
        }
        int size = registerRequestParamSaveRequestList.size();
        for (int i = 0; i < size; i++) {
            if(i == 0){
                address.append("?");
            }else {
                address.append("&");
            }
            address.append(registerRequestParamSaveRequestList.get(i).getFieldName()).append("=${").append(registerRequestParamSaveRequestList.get(i).getFieldName()).append("}");
        }
        return String.valueOf(address);
    }

    private String getDatasourceInterfaceAddress(IcreditApiBaseHiEntity apiBaseHiEntity, List<APIParamResult> params) {
        StringBuilder address = new StringBuilder(host).append("/v").append(apiBaseHiEntity.getApiVersion()).append("/").append(apiBaseHiEntity.getPath());
        int size = params.size();
        for (int i = 0; i < size; i++) {
            if(i == 0){
                address.append("?");
            }else{
                address.append("&");
            }
            //????????????like:ID=${ID}
            address.append(params.get(i).getFieldName()).append("=${").append(params.get(i).getFieldName()).append("}");
        }
        return String.valueOf(address);
    }

    @Override
    @Transactional
    public BusinessResult<ApiSaveResult> updateApi(String userId, DatasourceApiSaveParam param) {
        StringLegalUtils.checkId(param.getApiHiId());
        checkApiName(new CheckApiNameRequest(param.getApiHiId(), param.getName()));
        IcreditApiBaseHiEntity apiBaseHiEntity = apiBaseHiMapper.selectById(param.getApiHiId());
        if (!apiBaseHiEntity.getRequestType().equals(param.getRequestType())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000059.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000059.getMessage());
        }
        IcreditApiBaseEntity apiBaseEntity = apiBaseService.getById(apiBaseHiEntity.getApiBaseId());
        if (ApiHisOverrideEnum.OVERRIDE.getCode().equals(param.getOverride())){
            if(ApiTypeEnum.API_GENERATE.getCode().equals(param.getType())) {
                param.getApiGenerateSaveRequest().setId(generateApiService.getByApiIdAndVersion(apiBaseHiEntity.getApiBaseId(), apiBaseHiEntity.getApiVersion()).getId());
            }
            if(ApiTypeEnum.API_REGISTER.getCode().equals(param.getType())) {
                param.getApiGenerateSaveRequest().setId(registerApiService.findByApiIdAndApiVersion(apiBaseHiEntity.getApiBaseId(), apiBaseHiEntity.getApiVersion()).getId());
            }
        }
        //????????????????????????????????????
        if(ApiPublishStatusEnum.WAIT_PUBLISH.getCode().equals(apiBaseHiEntity.getPublishStatus()) || ApiHisOverrideEnum.OVERRIDE.getCode().equals(param.getOverride())){
            param.setApiVersion(apiBaseHiEntity.getApiVersion());
            ApiSaveResult apiSaveResult;
            String apiId = apiBaseEntity.getId();
            String apiBaseHiId = apiBaseHiEntity.getId();
            apiParamService.removeByApiIdAndApiVersion(apiId, apiBaseHiEntity.getApiVersion());
            generateApiService.removeByApiIdAndApiVersion(apiId, apiBaseHiEntity.getApiVersion());
            registerApiService.deleteByApiIdAndApiVersion(apiId, apiBaseHiEntity.getApiVersion());
            if(apiBaseHiEntity.getApiVersion().equals(apiBaseEntity.getApiVersion())){//?????????????????????????????????apiEntity???apiHiEntity???generateApi???apiParam
                BeanUtils.copyProperties(param, apiBaseEntity);
                apiBaseEntity.setId(apiId);
                if (ApiSaveStatusEnum.API_SAVE.getCode().equals(param.getSaveType())) {//??????
                    apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.WAIT_PUBLISH.getCode());
                    apiBaseEntity.setPublishUser("");
                    apiBaseEntity.setPublishTime(null);
                } else {
                    apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
                    apiBaseEntity.setPublishUser(userId);
                    apiBaseEntity.setPublishTime(new Date());
                }
                BeanUtils.copyProperties(apiBaseEntity, apiBaseHiEntity);
                apiBaseHiEntity.setId(apiBaseHiId);
                apiBaseHiEntity.setApiBaseId(apiId);
                apiBaseEntity.setInterfaceSource(InterfaceSourceEnum.IN_SIDE.getCode());
                apiBaseService.saveOrUpdate(apiBaseEntity);
                apiBaseHiEntity.setInterfaceSource(InterfaceSourceEnum.IN_SIDE.getCode());
                saveOrUpdate(apiBaseHiEntity);
                apiSaveResult = apiBaseService.saveApi(userId, param, apiBaseEntity);
                apiSaveResult.setApiHiId(apiBaseHiEntity.getId());
            }else{//?????????????????????????????????apiHiEntity???generateApi???apiParam
                BeanUtils.copyProperties(param, apiBaseHiEntity);
                apiBaseHiEntity.setId(apiBaseHiId);
                apiBaseHiEntity.setApiBaseId(apiId);
                if (ApiSaveStatusEnum.API_SAVE.getCode().equals(param.getSaveType())) {//??????
                    apiBaseHiEntity.setPublishStatus(ApiPublishStatusEnum.WAIT_PUBLISH.getCode());
                } else {
                    apiBaseHiEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
                    apiBaseHiEntity.setPublishUser(userId);
                    apiBaseHiEntity.setPublishTime(new Date());
                }
                apiBaseHiEntity.setInterfaceSource(InterfaceSourceEnum.IN_SIDE.getCode());
                saveOrUpdate(apiBaseHiEntity);
                IcreditApiBaseEntity newApiBaseEntity = new IcreditApiBaseEntity();
                BeanUtils.copyProperties(apiBaseHiEntity, newApiBaseEntity);
                newApiBaseEntity.setId(apiBaseHiEntity.getApiBaseId());
                apiSaveResult = apiBaseService.saveApi(userId, param, apiBaseEntity);
                apiSaveResult.setApiHiId(apiBaseHiEntity.getId());
            }
            return BusinessResult.success(apiSaveResult);
        }else {
            //??????apiId????????????????????????api???????????????????????????????????????????????????????????????????????????+1??????
            if(checkHaveSameApi(apiBaseHiEntity.getApiBaseId(), param)){//true--?????????????????????????????????api???false--????????????
                ApiSaveResult apiSaveResult = new ApiSaveResult();
                BeanUtils.copyProperties(param, apiSaveResult);
                ApiGenerateSaveResult generateApiSaveResult = new ApiGenerateSaveResult();
                BeanUtils.copyProperties(param.getApiGenerateSaveRequest(), generateApiSaveResult);
                apiSaveResult.setApiGenerateSaveRequest(generateApiSaveResult);
                List<ApiParamSaveResult> apiParamSaveResultList = BeanCopyUtils.copy(param.getApiParamSaveRequestList(), ApiParamSaveResult.class);
                List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList = BeanCopyUtils.copy(param.getRegisterRequestParamSaveRequestList(), RegisterRequestParamSaveRequest.class);
                List<RegisterResponseParamSaveRequest> registerResponseParamSaveRequestList = BeanCopyUtils.copy(param.getRegisterResponseParamSaveRequestList(), RegisterResponseParamSaveRequest.class);
                apiSaveResult.setApiParamSaveRequestList(apiParamSaveResultList);
                apiSaveResult.setRegisterRequestParamSaveRequestList(registerRequestParamSaveRequestList);
                apiSaveResult.setRegisterResponseParamSaveRequestList(registerResponseParamSaveRequestList);
                return BusinessResult.success(apiSaveResult);
            }

            //?????????????????????????????????????????? ??????????????????+1
            param.setApiVersion(apiBaseEntity.getApiVersion() + 1);
            log.info("apiBaseEntity????????????{}", param.getApiVersion());
            //??????api??????????????????api????????????
            IcreditApiBaseEntity newApiBaseEntity = new IcreditApiBaseEntity();
            BeanUtils.copyProperties(param, newApiBaseEntity);
            if (ApiSaveStatusEnum.API_SAVE.getCode().equals(param.getSaveType())) {//??????
                newApiBaseEntity.setPublishStatus(ApiPublishStatusEnum.WAIT_PUBLISH.getCode());
            } else {
                newApiBaseEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
                newApiBaseEntity.setPublishUser(userId);
                newApiBaseEntity.setPublishTime(new Date());
            }

            newApiBaseEntity.setId(apiBaseHiEntity.getApiBaseId());
            IcreditApiBaseHiEntity newApiBaseHiEntity = new IcreditApiBaseHiEntity();
            BeanUtils.copyProperties(newApiBaseEntity, newApiBaseHiEntity);
            newApiBaseEntity.setInterfaceSource(InterfaceSourceEnum.IN_SIDE.getCode());
            log.info("base???newApiBaseEntity:{}", newApiBaseEntity);
            apiBaseService.updateById(newApiBaseEntity);
            newApiBaseHiEntity.setApiBaseId(newApiBaseHiEntity.getId());
            newApiBaseHiEntity.setId(null);
            newApiBaseHiEntity.setInterfaceSource(InterfaceSourceEnum.IN_SIDE.getCode());
            log.info("?????????newApiBaseHiEntity:{}", newApiBaseHiEntity);
            save(newApiBaseHiEntity);
            ApiSaveResult apiSaveResult = apiBaseService.saveApi(userId, param, newApiBaseEntity);
            apiSaveResult.setApiHiId(newApiBaseHiEntity.getId());
            return BusinessResult.success(apiSaveResult);
        }
    }

    private void checkApiName(CheckApiNameRequest request) {
        if (!request.getName().matches("[a-zA-Z0-9\u4e00-\u9fa5_]{2,50}")) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000002.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000002.getMessage());
        }
        IcreditApiBaseHiEntity apiBaseHiEntity = apiBaseHiMapper.selectById(request.getId());
        List<IcreditApiBaseHiEntity> apiBaseEntityList = apiBaseHiMapper.findByApiBaseIdAndName(apiBaseHiEntity.getApiBaseId(), request.getName());
        if(!CollectionUtils.isEmpty(apiBaseEntityList)){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000003.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000003.getMessage());
        }
    }

    private boolean checkHaveSameApi(String apiBaseId, DatasourceApiSaveParam param) {
        LinkedList<ApiParamInfoDTO> apiInfoStrList = null;
        String apiInfoStr = "";
        if(ApiTypeEnum.API_REGISTER.getCode().equals(param.getType())){//??????api
            apiInfoStrList = apiParamService.findWaitPublishedByApiId(apiBaseId);
            LinkedList<RegisterApiDTO> registerApiDTOS = registerApiService.findWaitPublishedByApiId(apiBaseId);
            LinkedList<String> registerApiStrList = generateRegisterApi(registerApiDTOS, apiInfoStrList);
            apiInfoStr = String.valueOf(new StringBuilder(param.getName()).append(param.getDesc()).append(param.getReqHost())
                    .append(param.getReqPath()).append(generateForRegisterApi(param.getRegisterRequestParamSaveRequestList(), param.getRegisterResponseParamSaveRequestList())));
            for (String registerApiStr : registerApiStrList) {
                if(apiInfoStr.equals(registerApiStr)){
                    return true;
                }
            }
        }else{//?????????api
            LinkedList<DatasourceApiDTO> datasourceApiDTOS = generateApiService.findWaitPublishedByApiId(apiBaseId);
            if(ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())){//????????????
                apiInfoStrList = apiParamService.findWaitPublishedByApiId(apiBaseId);
                LinkedList<String> singleTableApiStrList = generateSingleTableApi(datasourceApiDTOS, apiInfoStrList);
                apiInfoStr = String.valueOf(new StringBuilder(param.getName()).append(param.getDesc()).append(param.getApiGenerateSaveRequest().getDatasourceId())
                        .append(param.getApiGenerateSaveRequest().getTableName()).append(generateForSingleTableApi(param.getApiParamSaveRequestList())));
                for (String singleTableApiStr : singleTableApiStrList) {
                    if(apiInfoStr.equals(singleTableApiStr)){
                        return true;
                    }
                }
            }else{//sql??????
                apiInfoStr = String.valueOf(new StringBuilder(param.getName()).append(param.getDesc()).append(param.getApiGenerateSaveRequest().getDatasourceId()).append(param.getApiGenerateSaveRequest().getSql()));
                for (DatasourceApiDTO datasourceApiDTO : datasourceApiDTOS) {
                    StringBuilder datasourceApiStr = new StringBuilder(datasourceApiDTO.getApiName()).append(datasourceApiDTO.getApiDesc()).append(datasourceApiDTO.getDatasourceId()).append(datasourceApiDTO.getQuerySql());
                    if(apiInfoStr.equals(String.valueOf(datasourceApiStr))){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private LinkedList<String> generateRegisterApi(LinkedList<RegisterApiDTO> registerApiDTOS, LinkedList<ApiParamInfoDTO> apiInfoStrList) {
        LinkedList<String> registerApiStrList = new LinkedList<>();
        for (RegisterApiDTO registerApiDTO : registerApiDTOS) {
            StringBuilder registerApiStr = new StringBuilder(registerApiDTO.getApiName()).append(registerApiDTO.getApiDesc()).append(registerApiDTO.getReqHost()).append(registerApiDTO.getReqPath());
            for (ApiParamInfoDTO apiParamInfoDTO : apiInfoStrList) {
                if(apiParamInfoDTO.getApiVersion().equals(registerApiDTO.getApiVersion())){
                    registerApiStr.append(apiParamInfoDTO.getApiParamStr()).append(COMMA);
                }
            }
            registerApiStrList.add(registerApiStr.indexOf(COMMA) != -1 ? String.valueOf(registerApiStr.substring(0, registerApiStr.lastIndexOf(COMMA))) : String.valueOf(registerApiStr));
        }
        return registerApiStrList;
    }

    private LinkedList<String> generateSingleTableApi(LinkedList<DatasourceApiDTO> datasourceApiDTOS, LinkedList<ApiParamInfoDTO> apiInfoStrList) {
        LinkedList<String> singleTableApiStrList = new LinkedList<>();
        for (DatasourceApiDTO datasourceApiDTO : datasourceApiDTOS) {
            StringBuilder singleTableApiStr = new StringBuilder(datasourceApiDTO.getApiName()).append(datasourceApiDTO.getApiDesc()).append(datasourceApiDTO.getDatasourceId()).append(datasourceApiDTO.getTableName());
            for (ApiParamInfoDTO apiParamInfoDTO : apiInfoStrList) {
                if(apiParamInfoDTO.getApiVersion().equals(datasourceApiDTO.getApiVersion())){
                    singleTableApiStr.append(apiParamInfoDTO.getApiParamStr()).append(COMMA);
                }
            }
            singleTableApiStrList.add(singleTableApiStr.indexOf(COMMA) != -1 ? String.valueOf(singleTableApiStr.substring(0, singleTableApiStr.lastIndexOf(COMMA))) : String.valueOf(singleTableApiStr));
        }
        return singleTableApiStrList;
    }

    private String generateForSingleTableApi(List<DatasourceApiParamSaveRequest> apiParamSaveRequestList) {
        StringBuilder apiParamStr = new StringBuilder();
        for (DatasourceApiParamSaveRequest datasourceApiParamSaveRequest : apiParamSaveRequestList) {
            apiParamStr.append(datasourceApiParamSaveRequest.getFieldName()).append(COMMA);
            apiParamStr.append(datasourceApiParamSaveRequest.getFieldType()).append(COMMA);
            apiParamStr.append(datasourceApiParamSaveRequest.getRequired()).append(COMMA);
            apiParamStr.append(datasourceApiParamSaveRequest.getIsRequest()).append(COMMA);
            apiParamStr.append(datasourceApiParamSaveRequest.getIsResponse()).append(COMMA);
            if(!StringUtils.isEmpty(datasourceApiParamSaveRequest.getDesc())){
                apiParamStr.append(datasourceApiParamSaveRequest.getDesc()).append(COMMA);
            }
        }
        String tableInfoStr = String.valueOf(new StringBuilder(apiParamStr));
        return tableInfoStr.substring(0, tableInfoStr.lastIndexOf(COMMA));
    }

    private String generateForRegisterApi(List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList, List<RegisterResponseParamSaveRequest> registerResponseParamSaveRequestList) {
        StringBuilder apiParamStr = new StringBuilder();
        for (RegisterRequestParamSaveRequest registerRequestParamSaveRequest : registerRequestParamSaveRequestList) {
            apiParamStr.append(registerRequestParamSaveRequest.getFieldName()).append(COMMA);
            apiParamStr.append(registerRequestParamSaveRequest.getFieldType()).append(COMMA);
            if(null != registerRequestParamSaveRequest.getRequired()){
                apiParamStr.append(registerRequestParamSaveRequest.getRequired()).append(COMMA);
            }
            apiParamStr.append(registerRequestParamSaveRequest.getIsRequest()).append(COMMA);
            apiParamStr.append(registerRequestParamSaveRequest.getDefaultValue()).append(COMMA);
            apiParamStr.append(registerRequestParamSaveRequest.getDesc()).append(COMMA);
        }
        for (RegisterResponseParamSaveRequest registerResponseParamSaveRequest : registerResponseParamSaveRequestList) {
            apiParamStr.append(registerResponseParamSaveRequest.getFieldName()).append(COMMA);
            apiParamStr.append(registerResponseParamSaveRequest.getFieldType()).append(COMMA);
            apiParamStr.append(registerResponseParamSaveRequest.getIsResponse()).append(COMMA);
            apiParamStr.append(registerResponseParamSaveRequest.getDefaultValue()).append(COMMA);
            apiParamStr.append(registerResponseParamSaveRequest.getDesc()).append(COMMA);
        }
        String tableInfoStr = String.valueOf(new StringBuilder(apiParamStr));
        return tableInfoStr.length() >= 1 ? tableInfoStr.substring(0, tableInfoStr.lastIndexOf(COMMA)) : "";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<Boolean> deleteBatch(String userId, String[] apiHiIdsArry) {
        List<String> apiHiIds = Arrays.asList(apiHiIdsArry);
        if (CollectionUtils.isEmpty(apiHiIds)){
            return BusinessResult.success(true);
        }

        for (String apiHiId : apiHiIds) {
            deleteByUserIdAndId(userId, apiHiId);
        }
        return BusinessResult.success(true);
    }

    private void deleteByUserIdAndId(String userId, String apiHiId) {
        IcreditApiBaseHiEntity entity = getById(apiHiId);
        IcreditApiBaseEntity apiBaseEntity = apiBaseService.getById(entity.getApiBaseId());
        if (InterfaceSourceEnum.OUT_SIDE.getCode().equals(apiBaseEntity.getInterfaceSource())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000052.getCode());
        }
        if (ApiPublishStatusEnum.PUBLISHED.getCode().equals(entity.getPublishStatus())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000051.getCode());
        }
        entity.setUpdateBy(userId);
        entity.setUpdateTime(new Date());
        entity.setDelFlag(DelFlagEnum.DIS_ABLED.getCode());
        apiBaseHiMapper.deleteByEntity(entity);
        //??????param?????????????????????api????????????????????????api??????
        apiParamService.removeByApiIdAndApiVersion(apiBaseEntity.getId(), entity.getApiVersion());
        generateApiService.deleteByApiIdAndVersion(apiBaseEntity.getId(), entity.getApiVersion());
        registerApiService.deleteByApiIdAndApiVersion(apiBaseEntity.getId(), entity.getApiVersion());
        List<IcreditApiBaseHiEntity> apiBaseHiEntityList = apiBaseHiMapper.listByApiBaseId(entity.getApiBaseId());
        //??????????????????????????????????????????API
        if (CollectionUtils.isEmpty(apiBaseHiEntityList)){
            apiBaseService.removeById(entity.getApiBaseId());
        }else {
            //?????????????????????????????????????????????API??????????????????????????? - 1???API??????
            IcreditApiBaseHiEntity apiBaseHiNewest = apiBaseHiEntityList.get(0);
            if (!apiBaseEntity.getApiVersion().equals(apiBaseHiNewest.getApiVersion())){
                String apiBaseId = apiBaseEntity.getId();
                BeanCopyUtils.copyProperties(apiBaseHiNewest, apiBaseEntity);
                apiBaseEntity.setId(apiBaseId);
                apiBaseService.updateById(apiBaseEntity);
            }
        }
        redisTemplate.delete(String.valueOf(new StringBuilder(entity.getPath()).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(entity.getApiVersion())));
        redisTemplate.opsForZSet().remove(apiUsedCount, entity.getApiBaseId());//??????????????????
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<Boolean> deleteById(String userId, String apiHiId) {
        if (StringUtils.isBlank(apiHiId)){
            return BusinessResult.success(true);
        }
        deleteByUserIdAndId(userId, apiHiId);
        return BusinessResult.success(true);
    }

    @Override
    public void removeByApiBaseId(String id) {
        apiBaseHiMapper.removeByApiBaseId(id);
    }
}
