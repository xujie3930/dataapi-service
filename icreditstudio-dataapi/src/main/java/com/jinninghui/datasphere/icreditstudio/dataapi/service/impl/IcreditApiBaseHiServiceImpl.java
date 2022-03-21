package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiPublishStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiSaveStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.RequestFiledEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseHiMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.factory.ApiBaseFactory;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.*;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * API表基础信息历史版本表 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditApiBaseHiServiceImpl extends ServiceImpl<IcreditApiBaseHiMapper, IcreditApiBaseHiEntity> implements IcreditApiBaseHiService {

    @Resource
    private IcreditApiBaseHiMapper apiBaseHiMapper;
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

    @Override
    public IcreditApiBaseHiEntity findByApiBaseId(String apiId) {
        return apiBaseHiMapper.findByApiBaseId(apiId);
    }

    @Override
    @ResultReturning
    public BusinessResult<BusinessPageResult<ApiHistoryListResult>> getList(ApiHistoryListRequest request) {
        StringLegalUtils.checkId(request.getApiId());
        request.setPageStartNum((request.getPageNum() - 1) * request.getPageSize());
        List<ApiHistoryListResult> apiHistoryListResultList = apiBaseHiMapper.getList(request);
        Long apiBaseHiCount = apiBaseHiMapper.countApiBaseHi(request);
        return BusinessResult.success(BusinessPageResult.build(apiHistoryListResultList, request, apiBaseHiCount));
    }

    @Override
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
        result.setId(null);
        //根据不同API类型返回不同对象
        ApiBaseService apiService = apiBaseFactory.getApiService(apiBaseHiEntity.getType());
        apiService.setApiBaseResult(result);
        //获取其业务流程和分组名称
        IcreditApiGroupEntity apiGroupEntity = apiGroupService.getById(apiBaseHiEntity.getApiGroupId());
        if (!Objects.isNull(apiGroupEntity)) {
            result.setApiGroupName(apiGroupEntity.getName());
            result.setApiGroupId(apiGroupEntity.getId());
            IcreditWorkFlowEntity workFlowEntity = workFlowService.getById(apiGroupEntity.getWorkId());
            result.setWorkFlowName(workFlowEntity.getName());
            result.setWorkFlowId(workFlowEntity.getId());
        }
        //获取其param参数
        List<IcreditApiParamEntity> apiParamEntityList = apiParamService.getByApiIdAndVersion(apiBaseHiEntity.getApiBaseId(), apiBaseHiEntity.getApiVersion());
        List<APIParamResult> apiParamList = new ArrayList<>();
        List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList = new ArrayList<>();
        List<RegisterResponseParamSaveRequest> registerResponseParamSaveRequestList = new ArrayList<>();
        String address;
        if(ApiTypeEnum.API_GENERATE.getCode().equals(apiBaseHiEntity.getType())) {
            apiParamList = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.copy(apiParamEntityList, APIParamResult.class);
            List<APIParamResult> params = apiParamList.stream()
                    .filter((APIParamResult a) -> RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(a.getIsRequest()))
                    .collect(Collectors.toList());
            address = getDatasourceInterfaceAddress(apiBaseHiEntity, params);
        }else{
            apiBaseService.handleRegisterApiParamInfo(registerRequestParamSaveRequestList, registerResponseParamSaveRequestList, apiParamEntityList);
            address = getRegisterInterfaceAddress(apiBaseHiEntity, registerRequestParamSaveRequestList);
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

    private String getRegisterInterfaceAddress(IcreditApiBaseHiEntity apiBaseHiEntity, List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList){
        StringBuilder address = new StringBuilder(apiBaseHiEntity.getReqHost()).append("/").append(apiBaseHiEntity.getReqPath());
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
            //参数拼接like:ID=${ID}
            address.append(params.get(i).getFieldName()).append("=${").append(params.get(i).getFieldName()).append("}");
        }
        return String.valueOf(address);
    }

    @Override
    @Transactional
    public BusinessResult<ApiSaveResult> updateApi(String userId, DatasourceApiSaveParam param) {
        StringLegalUtils.checkId(param.getApiHiId());
        IcreditApiBaseHiEntity apiBaseHiEntity = apiBaseHiMapper.selectById(param.getApiHiId());
        IcreditApiBaseEntity apiBaseEntity = apiBaseService.getById(apiBaseHiEntity.getApiBaseId());
        //todo 待发布的编辑，版本号不变
        if(ApiPublishStatusEnum.WAIT_PUBLISH.getCode().equals(apiBaseHiEntity.getPublishStatus())){
            param.setApiVersion(apiBaseHiEntity.getApiVersion());
            if(apiBaseHiEntity.getApiVersion().equals(apiBaseEntity.getApiVersion())){//编辑最新版本，需要更新apiEntity、apiHiEntity、generateApi、apiParam

            }else{//编辑历史版本，需要更新apiHiEntity、generateApi、apiParam

            }
            return null;
        }else {
            //未发布、已发布的编辑，版本号 为最新版本号+1
            param.setApiVersion(apiBaseEntity.getApiVersion() + 1);

            //保存api基础信息、及api历史信息
            IcreditApiBaseEntity newApiBaseEntity = new IcreditApiBaseEntity();
            BeanUtils.copyProperties(param, newApiBaseEntity);
            if (ApiSaveStatusEnum.API_SAVE.getCode().equals(param.getSaveType())) {//保存
                newApiBaseEntity.setPublishStatus(ApiPublishStatusEnum.WAIT_PUBLISH.getCode());
            } else {
                newApiBaseEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
                newApiBaseEntity.setPublishUser(userId);
                newApiBaseEntity.setPublishTime(new Date());
            }
            newApiBaseEntity.setId(apiBaseHiEntity.getApiBaseId());
            IcreditApiBaseHiEntity newApiBaseHiEntity = new IcreditApiBaseHiEntity();
            BeanUtils.copyProperties(newApiBaseEntity, newApiBaseHiEntity);
            apiBaseService.updateById(newApiBaseEntity);
            newApiBaseHiEntity.setApiBaseId(newApiBaseHiEntity.getId());
            newApiBaseHiEntity.setId(null);
            save(newApiBaseHiEntity);
            ApiSaveResult apiSaveResult = apiBaseService.saveApi(userId, param, newApiBaseEntity);
            apiSaveResult.setApiHiId(newApiBaseHiEntity.getId());
            return BusinessResult.success(apiSaveResult);
        }
    }

    @Override
    public BusinessResult<ApiVersionsResult> apiVersions(ApiVersionsRequest request) {
        StringLegalUtils.checkId(request.getApiId());
        List<Integer> apiVersions = apiBaseHiMapper.getApiVersionsByApiId(request.getApiId());
        ApiVersionsResult apiVersionsResult = new ApiVersionsResult();
        apiVersionsResult.setApiVersions(apiVersions);
        return BusinessResult.success(apiVersionsResult);
    }
}
