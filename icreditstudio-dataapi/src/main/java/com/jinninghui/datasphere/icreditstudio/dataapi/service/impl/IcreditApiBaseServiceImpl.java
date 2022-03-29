package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.FieldInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.DatasourceFeignClient;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DataSourceInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DatasourceDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.ConnectionInfoVO;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.bo.CreateApiInfoBO;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RegisterApiParamInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.bo.TableFieldBO;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.bo.TableNameInfoBO;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.DBConnectionManager;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.*;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.Query;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.springframework.jdbc.support.JdbcUtils.closeConnection;

/**
 * <p>
 * API表基础信息表 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Slf4j
@Service
public class IcreditApiBaseServiceImpl extends ServiceImpl<IcreditApiBaseMapper, IcreditApiBaseEntity> implements IcreditApiBaseService {

    @Resource
    private IcreditGenerateApiService generateApiService;
    @Resource
    private IcreditRegisterApiService registerApiService;
    @Resource
    private IcreditApiBaseHiService apiBaseHiService;
    @Resource
    private IcreditApiParamService apiParamService;
    @Resource
    private DatasourceFeignClient dataSourceFeignClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private IcreditApiBaseMapper apiBaseMapper;
    @Value("${host.addr}")
    private String host;

    private static final String EMPTY_CHAR = " ";
    private static final String MANY_EMPTY_CHAR = " +";
    private static final String SQL_START = "select ";
    private static final String SQL_ON = " on ";
    private static final String SQL_AS = " as ";
    private static final String SQL_SELECT_ALL = "select *";
    private static final String SQL_AND = " and ";
    private static final String SQL_FIELD_SPLIT_CHAR = ",";
    private static final String SQL_WHERE = " where ";
    private static final String SQL_FROM = " from ";
    private static final String SQL_END = ";";
    private static final String SQL_LIMIT = " limit 1";
    private static final String SQL_CONN_CHAR = ".";
    private static final String SEPARATOR = "|";
    private static final String PERCENTAGE = "%";
    private static final String SPLIT_URL_FLAG = "?";
    private static final String SQL_CHARACTER = "useSSL=false&useUnicode=true&characterEncoding=utf8";
    private static final String REDIS_KEY_SPLIT_JOINT_CHAR = ":";
    private static final String DEFAULT_API_GROUP = "000";
    private static final String SQL_COUNT_ONE = "count(1)";
    private static final String SQL_COUNT_ALL = "count(*)";

    @Override
    @ResultReturning
    public BusinessResult<BusinessPageResult> getList(ApiBaseListRequest request) {
        Wrapper<IcreditApiBaseEntity> wrapper = queryWrapper(request);
        IPage<IcreditApiBaseEntity> page = this.page(
                new Query<IcreditApiBaseEntity>().getPage(request),
                wrapper
        );
        IPage<ApiBaseResult> resultIPage = new Page<>();
        List<IcreditApiBaseEntity> records = page.getRecords();
        List<ApiBaseResult> ApiBaseResultList = new LinkedList<>();
        for (IcreditApiBaseEntity record : records) {
            ApiBaseResult apiBaseResult = BeanCopyUtils.copyProperties(record, new ApiBaseResult());
            if(null != record.getPublishTime()) {
                apiBaseResult.setPublishTime(record.getPublishTime().getTime());
            }
            ApiBaseResultList.add(apiBaseResult);
        }
        resultIPage.setRecords(ApiBaseResultList);
        resultIPage.setPages(page.getPages());
        resultIPage.setTotal(page.getTotal());
        return BusinessResult.success(BusinessPageResult.build(resultIPage, request));
    }

    private Wrapper<IcreditApiBaseEntity> queryWrapper(ApiBaseListRequest request) {
        QueryWrapper<IcreditApiBaseEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(request.getName())){
            wrapper.like(IcreditApiBaseEntity.NAME, request.getName());
        }
        if (StringUtils.isNotBlank(request.getPath())){
            wrapper.like(IcreditApiBaseEntity.PATH, request.getPath());
        }
        if (null != request.getPublishStatus()){
            wrapper.eq(IcreditApiBaseEntity.PUBLISH_STATUS, request.getPublishStatus());
        }
        if (null != request.getType()){
            wrapper.eq(IcreditApiBaseEntity.TYPE, request.getType());
        }
        if (null != request.getPublishTimeStart() && null != request.getPublishTimeEnd()){
            Date startTime = DateUtils.parseDate(DateUtils.longToString(request.getPublishTimeStart()) + " 00:00:00");
            Date endTime = DateUtils.parseDate(DateUtils.longToString(request.getPublishTimeEnd()) + " 23:59:59");
            wrapper.ge(IcreditApiBaseEntity.PUBLISH_TIME, startTime).le(IcreditApiBaseEntity.PUBLISH_TIME, endTime);
        }
        wrapper.orderByDesc(IcreditApiBaseEntity.CREATE_TIME);
        wrapper.eq(IcreditApiBaseEntity.API_GROUP_ID, request.getApiGroupId());
        return wrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<ApiSaveResult> createApi(String userId, DatasourceApiSaveParam param) {
        checkApiName(new CheckApiNameRequest(param.getId(), param.getName()));
        checkApiPath(new CheckApiPathRequest(param.getId(), param.getPath()));
        param.setApiVersion(1);
        //保存api基础信息、及api历史信息
        IcreditApiBaseEntity apiBaseEntity = new IcreditApiBaseEntity();
        BeanUtils.copyProperties(param, apiBaseEntity);
        if (ApiSaveStatusEnum.API_SAVE.getCode().equals(param.getSaveType())) {//保存
            apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.WAIT_PUBLISH.getCode());
        } else {
            apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
            apiBaseEntity.setPublishUser(userId);
            apiBaseEntity.setPublishTime(new Date());
        }
        apiBaseEntity.setInterfaceSource(InterfaceSourceEnum.IN_SIDE.getCode());
        saveOrUpdate(apiBaseEntity);
        IcreditApiBaseHiEntity apiBaseHiEntity = apiBaseHiService.findByApiBaseId(apiBaseEntity.getId());
        if(null == apiBaseHiEntity) {
            apiBaseHiEntity = new IcreditApiBaseHiEntity();
            BeanUtils.copyProperties(apiBaseEntity, apiBaseHiEntity);
        }else{
            String apiBaseHiId = apiBaseHiEntity.getId();
            BeanUtils.copyProperties(apiBaseEntity, apiBaseHiEntity);
            apiBaseHiEntity.setId(apiBaseHiId);
        }
        registerApiService.deleteByApiIdAndApiVersion(apiBaseEntity.getId(), apiBaseEntity.getApiVersion());
        apiBaseHiEntity.setInterfaceSource(InterfaceSourceEnum.IN_SIDE.getCode());
        apiBaseHiEntity.setApiBaseId(apiBaseEntity.getId());
        apiBaseHiService.saveOrUpdate(apiBaseHiEntity);
        apiParamService.removeByApiIdAndApiVersion(apiBaseEntity.getId(), apiBaseEntity.getApiVersion());
        return BusinessResult.success(saveApi(userId, param, apiBaseEntity));
    }

    @Override
    public ApiSaveResult saveApi(String userId, DatasourceApiSaveParam param, IcreditApiBaseEntity apiBaseEntity) {
        if(ApiTypeEnum.API_REGISTER.getCode().equals(param.getType())) {
            checkReqPath(param.getReqPath());
            checkReqHost(param.getReqHost());
        }
        CreateApiInfoBO createApiInfoBO = null;
        List<RegisterApiParamInfo> registerApiParamInfos = new ArrayList<>();
        IcreditGenerateApiEntity generateApiEntity = new IcreditGenerateApiEntity();
        if(ApiTypeEnum.API_GENERATE.getCode().equals(param.getType())) {//数据源生成api
            createApiInfoBO = createApiByModel(param, apiBaseEntity.getApiVersion(), apiBaseEntity.getId());
            //保存 generate api
            BeanUtils.copyProperties(param.getApiGenerateSaveRequest(), generateApiEntity);
            generateApiEntity.setId(param.getApiGenerateSaveRequest().getId());
            generateApiEntity.setApiBaseId(apiBaseEntity.getId());
            generateApiEntity.setApiVersion(apiBaseEntity.getApiVersion());
            if (null != createApiInfoBO && ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())) {
                generateApiEntity.setSql(createApiInfoBO.getQuerySql());
            }
            if (null != createApiInfoBO && ApiModelTypeEnum.SQL_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())) {
                generateApiEntity.setTableName(createApiInfoBO.getTableNames());
            }
            generateApiService.saveOrUpdate(generateApiEntity);
        }else{//注册api
            createApiInfoBO = createRegisterApi(param, apiBaseEntity.getApiVersion(), apiBaseEntity.getId());
            registerApiParamInfos = BeanCopyUtils.copy(createApiInfoBO.getApiParamEntityList(), RegisterApiParamInfo.class);
            IcreditRegisterApiEntity registerApiEntity = new IcreditRegisterApiEntity();
            registerApiEntity.setHost(param.getReqHost());
            registerApiEntity.setPath(param.getReqPath());
            registerApiEntity.setId(param.getId());
            registerApiEntity.setApiVersion(param.getApiVersion());
            registerApiEntity.setApiBaseId(apiBaseEntity.getId());
            registerApiService.saveOrUpdate(registerApiEntity);
        }

        if(null != createApiInfoBO && !CollectionUtils.isEmpty(createApiInfoBO.getApiParamEntityList())) {
            apiParamService.saveOrUpdateBatch(createApiInfoBO.getApiParamEntityList());
        }

        //发布操作 存放信息到redis
        if (ApiSaveStatusEnum.API_PUBLISH.getCode().equals(param.getSaveType())){
            if(ApiModelTypeEnum.SQL_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())){
                String sql = createApiInfoBO.getQuerySql().toLowerCase();
                String lowerCaseSqlCount = sql.substring(sql.indexOf(SQL_START) + SQL_START.length(), sql.indexOf(SQL_FROM));
                if(lowerCaseSqlCount.contains(SQL_COUNT_ONE) || lowerCaseSqlCount.contains(SQL_COUNT_ALL)){
                    String naturalSqlCount = createApiInfoBO.getQuerySql().substring(sql.indexOf(SQL_START) + SQL_START.length(), sql.indexOf(SQL_FROM));
                    String responseFields = lowerCaseSqlCount.contains(SQL_AS) ? naturalSqlCount.substring(lowerCaseSqlCount.indexOf(SQL_AS) + SQL_AS.length()) : lowerCaseSqlCount.contains(EMPTY_CHAR) ?
                            naturalSqlCount.substring(lowerCaseSqlCount.indexOf(EMPTY_CHAR) + EMPTY_CHAR.length()) : lowerCaseSqlCount;
                    saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseEntity.getPath(), apiBaseEntity.getName(),
                            apiBaseEntity.getType(), apiBaseEntity.getApiVersion(), createApiInfoBO.getQuerySql(), createApiInfoBO.getRequiredFieldStr(),
                            responseFields, registerApiParamInfos, param.getReqHost(), param.getReqPath());
                }else{
                    saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseEntity.getPath(), apiBaseEntity.getName(),
                            apiBaseEntity.getType(), apiBaseEntity.getApiVersion(), createApiInfoBO.getQuerySql(), createApiInfoBO.getRequiredFieldStr(),
                            createApiInfoBO.getResponseFieldStr(), registerApiParamInfos, param.getReqHost(), param.getReqPath());
                }
            }else{
                saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseEntity.getPath(), apiBaseEntity.getName(),
                        apiBaseEntity.getType(), apiBaseEntity.getApiVersion(), createApiInfoBO.getQuerySql(), createApiInfoBO.getRequiredFieldStr(),
                        createApiInfoBO.getResponseFieldStr(), registerApiParamInfos, param.getReqHost(), param.getReqPath());
            }
        }
        //返回参数
        ApiSaveResult apiSaveResult = new ApiSaveResult();
        apiSaveResult.setId(apiBaseEntity.getId());
        List<ApiParamSaveResult> apiParamSaveResultList = new ArrayList<>();
        List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList = new ArrayList<>();
        List<RegisterResponseParamSaveRequest> registerResponseParamSaveRequestList = new ArrayList<>();
        ApiGenerateSaveResult generateApiSaveResult = new ApiGenerateSaveResult();
        if(ApiTypeEnum.API_GENERATE.getCode().equals(param.getType())) {//数据源生成api
            apiParamSaveResultList = BeanCopyUtils.copy(createApiInfoBO.getApiParamEntityList(), ApiParamSaveResult.class);
            BeanUtils.copyProperties(generateApiEntity, generateApiSaveResult);
        }else{
            registerRequestParamSaveRequestList = BeanCopyUtils.copy(param.getRegisterRequestParamSaveRequestList(), RegisterRequestParamSaveRequest.class);
            registerResponseParamSaveRequestList = BeanCopyUtils.copy(param.getRegisterResponseParamSaveRequestList(), RegisterResponseParamSaveRequest.class);
        }
        apiSaveResult.setApiGenerateSaveRequest(generateApiSaveResult);
        apiSaveResult.setApiParamSaveRequestList(apiParamSaveResultList);
        apiSaveResult.setRegisterRequestParamSaveRequestList(registerRequestParamSaveRequestList);
        apiSaveResult.setRegisterResponseParamSaveRequestList(registerResponseParamSaveRequestList);
        return apiSaveResult;
    }

    private void checkReqPath(String reqPath) {
        if(!reqPath.matches("[/a-zA-Z0-9_-]+")){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000043.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000043.getMessage());
        }
    }

    private void checkReqHost(String reqHost) {
        if(StringUtils.isEmpty(reqHost) ||
                !reqHost.matches("^((http)://)((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(:([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$)")){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000042.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000042.getMessage());
        }
    }

    private CreateApiInfoBO createRegisterApi(DatasourceApiSaveParam param, Integer apiVersion, String apiId) {
        CreateApiInfoBO createApiInfoBO = new CreateApiInfoBO();
        List<IcreditApiParamEntity> apiParamEntityList = new ArrayList<>();
        for (RegisterRequestParamSaveRequest registerRequestParamSaveRequest : param.getRegisterRequestParamSaveRequestList()) {
            checkFieldRegisterApiParam(registerRequestParamSaveRequest.getFieldName(), registerRequestParamSaveRequest.getDefaultValue(), registerRequestParamSaveRequest.getDesc());
            IcreditApiParamEntity apiParamEntity = new IcreditApiParamEntity();
            apiParamEntity.setApiBaseId(apiId);
            apiParamEntity.setApiVersion(apiVersion);
            apiParamEntity.setDesc(registerRequestParamSaveRequest.getDesc());
            apiParamEntity.setRequired(registerRequestParamSaveRequest.getRequired());
            apiParamEntity.setFieldName(registerRequestParamSaveRequest.getFieldName());
            apiParamEntity.setFieldType(registerRequestParamSaveRequest.getFieldType());
            apiParamEntity.setIsRequest(registerRequestParamSaveRequest.getIsRequest());
            apiParamEntity.setDefaultValue(registerRequestParamSaveRequest.getDefaultValue());
            apiParamEntityList.add(apiParamEntity);
        }
        for (RegisterResponseParamSaveRequest registerResponseParamSaveRequest : param.getRegisterResponseParamSaveRequestList()) {
            checkFieldRegisterApiParam(registerResponseParamSaveRequest.getFieldName(), registerResponseParamSaveRequest.getDefaultValue(), registerResponseParamSaveRequest.getDesc());
            IcreditApiParamEntity apiParamEntity = new IcreditApiParamEntity();
            apiParamEntity.setApiBaseId(apiId);
            apiParamEntity.setApiVersion(apiVersion);
            apiParamEntity.setDesc(registerResponseParamSaveRequest.getDesc());
            apiParamEntity.setFieldName(registerResponseParamSaveRequest.getFieldName());
            apiParamEntity.setFieldType(registerResponseParamSaveRequest.getFieldType());
            apiParamEntity.setIsResponse(registerResponseParamSaveRequest.getIsResponse());
            apiParamEntity.setDefaultValue(registerResponseParamSaveRequest.getDefaultValue());
            apiParamEntityList.add(apiParamEntity);
        }
        createApiInfoBO.setApiParamEntityList(apiParamEntityList);
        return createApiInfoBO;
    }

    private void checkFieldRegisterApiParam(String fieldName, String defaultValue, String desc) {
        if(!fieldName.matches("[a-zA-Z\u4e00-\u9fa5]{1,50}")){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000044.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000044.getMessage());
        }
        if(defaultValue.length() > 100){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000045.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000045.getMessage());
        }
        if(desc.length() > 100){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000046.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000046.getMessage());
        }
    }

    private CreateApiInfoBO createApiByModel(DatasourceApiSaveParam param, Integer apiVersion, String apiId) {
        if(ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())){
            return createApiBySingleTableModel(param, apiVersion, apiId);
        }else{
            return createApiBySqlModel(param, apiVersion, apiId);
        }
    }

    //表单模式
    private CreateApiInfoBO createApiBySingleTableModel(DatasourceApiSaveParam param, Integer apiVersion, String apiId) {
        String querySql;
        String requiredFieldStr = null;
        String responseFieldStr = null;
        CreateApiInfoBO createApiInfoBO = new CreateApiInfoBO();
        StringBuffer requiredFields = new StringBuffer();//请求参数
        StringBuffer responseFields = new StringBuffer();//返回参数
        List<IcreditApiParamEntity> apiParamEntityList = new ArrayList<>();
        boolean isHaveRespField = false;
        StringBuffer querySqlPrefix = new StringBuffer(SQL_START);
        StringBuffer querySqlSuffix = new StringBuffer(SQL_WHERE);
        //保存 api param
        for (DatasourceApiParamSaveRequest datasourceApiParamSaveRequest : param.getApiParamSaveRequestList()) {
            IcreditApiParamEntity apiParamEntity = new IcreditApiParamEntity();
            apiParamEntity.setApiBaseId(apiId);
            apiParamEntity.setApiVersion(apiVersion);
            apiParamEntity.setDesc(datasourceApiParamSaveRequest.getDesc());
            apiParamEntity.setTableName(param.getApiGenerateSaveRequest().getTableName());
            apiParamEntity.setRequired(datasourceApiParamSaveRequest.getRequired());
            apiParamEntity.setFieldName(datasourceApiParamSaveRequest.getFieldName());
            apiParamEntity.setFieldType(datasourceApiParamSaveRequest.getFieldType());
            apiParamEntity.setIsRequest(datasourceApiParamSaveRequest.getIsRequest());
            apiParamEntity.setIsResponse(datasourceApiParamSaveRequest.getIsResponse());
            apiParamEntityList.add(apiParamEntity);
            if (ResponseFiledEnum.IS_RESPONSE_FIELD.getCode().equals(datasourceApiParamSaveRequest.getIsResponse())) {
                querySqlPrefix.append(datasourceApiParamSaveRequest.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                responseFields.append(datasourceApiParamSaveRequest.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                isHaveRespField = true;
            }
            if (RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(datasourceApiParamSaveRequest.getIsRequest())) {
                querySqlSuffix.append(datasourceApiParamSaveRequest.getFieldName())
                        .append(" = ${").append(datasourceApiParamSaveRequest.getFieldName()).append("}").append(SQL_AND);
            }
            if (RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(datasourceApiParamSaveRequest.getRequired())) {
                requiredFields.append(datasourceApiParamSaveRequest.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
            }
        }
        if (!isHaveRespField) {//没有勾选返回参数
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
        }
        querySql = String.valueOf(new StringBuffer(querySqlPrefix.substring(0, querySqlPrefix.lastIndexOf(SQL_FIELD_SPLIT_CHAR)))
                .append(SQL_FROM).append(param.getApiGenerateSaveRequest().getTableName()).append(querySqlSuffix));
        if (querySql.endsWith(SQL_WHERE)) {
            querySql = querySql.substring(0, querySql.lastIndexOf(SQL_WHERE));
        }
        if (querySql.endsWith(SQL_AND)) {
            querySql = querySql.substring(0, querySql.lastIndexOf(SQL_AND));
        }
        if (requiredFields.length() >= 1) {
            requiredFieldStr = String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
        }
        if (responseFields.length() >= 1) {
            responseFieldStr = String.valueOf(new StringBuffer(responseFields.substring(0, responseFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
        }
        createApiInfoBO.setRequiredFieldStr(requiredFieldStr);
        createApiInfoBO.setResponseFieldStr(responseFieldStr);
        createApiInfoBO.setQuerySql(querySql);
        createApiInfoBO.setApiParamEntityList(apiParamEntityList);
        return createApiInfoBO;
    }

    //sql模式
    private CreateApiInfoBO createApiBySqlModel(DatasourceApiSaveParam param, Integer apiVersion, String apiId){
        String querySql;
        String requiredFieldStr = null;
        String responseFieldStr = null;
        StringBuffer requiredFields = new StringBuffer();//请求参数
        StringBuffer responseFields = new StringBuffer();//返回参数
        CreateApiInfoBO createApiInfoBO = (CreateApiInfoBO) checkQuerySql(new CheckQuerySqlRequest(param.getApiGenerateSaveRequest().getDatasourceId(), param.getApiGenerateSaveRequest().getSql()), apiId, apiVersion, QuerySqlCheckType.NEED_GET_TABLE_FIELD.getCode());
        List<IcreditApiParamEntity> apiParamEntityList = createApiInfoBO.getApiParamEntityList();
        querySql = param.getApiGenerateSaveRequest().getSql().replaceAll(MANY_EMPTY_CHAR, EMPTY_CHAR).toLowerCase().replaceAll(SQL_END, "");
        String[] tableNames = null;
        String[] responseFieldArr = querySql.substring(SQL_START.length(), querySql.indexOf(SQL_FROM)).split(SQL_FIELD_SPLIT_CHAR);
        String[] requiredFieldArr;
        if(querySql.contains(SQL_WHERE)) {
            requiredFieldArr = querySql.substring(querySql.indexOf(SQL_WHERE) + SQL_WHERE.length()).split(SQL_AND);
            tableNames = querySql.substring(querySql.indexOf(SQL_FROM) + SQL_FROM.length(), querySql.indexOf(SQL_WHERE)).replaceAll(" left join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" right join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" full join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" inner join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" join ", SQL_FIELD_SPLIT_CHAR).split(SQL_FIELD_SPLIT_CHAR);
        }else{
            requiredFieldArr = new String[]{};
            tableNames = querySql.substring(querySql.indexOf(SQL_FROM) + SQL_FROM.length()).replaceAll(" left join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" right join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" full join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" inner join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" join ", SQL_FIELD_SPLIT_CHAR).split(SQL_FIELD_SPLIT_CHAR);
        }
        for (String requiredField : requiredFieldArr) {
            if(requiredField.contains("${")) {
                requiredFields.append(requiredField.contains(" =") ? requiredField.substring(0, requiredField.indexOf(" =")) : requiredField.substring(0, requiredField.indexOf("="))).append(SQL_FIELD_SPLIT_CHAR);
            }
        }
        List<TableNameInfoBO> tableNameInfoBOList = new ArrayList<>(tableNames.length);
        for (String tableName : tableNames) {
            TableNameInfoBO tableNameInfoBO = new TableNameInfoBO();
            tableName = tableName.contains(SQL_ON) ? tableName.substring(0, tableName.indexOf(SQL_ON)) : tableName;
            tableName = tableName.startsWith(EMPTY_CHAR) ? tableName.trim() : tableName;
            String tableAlia = tableName.contains(SQL_AS) ? tableName.substring(tableName.indexOf(SQL_AS) + SQL_AS.length()) : tableName.contains(EMPTY_CHAR) ? tableName.substring(tableName.indexOf(EMPTY_CHAR) + 1) : "";
            tableNameInfoBO.setTableAlias(tableAlia.replaceAll(EMPTY_CHAR, ""));
            tableNameInfoBO.setTableName(tableName.contains(EMPTY_CHAR) ? tableName.substring(0, tableName.indexOf(EMPTY_CHAR)) : tableName);
            tableNameInfoBOList.add(tableNameInfoBO);
        }
        for (String responseField : responseFieldArr) {
            responseField = responseField.startsWith(EMPTY_CHAR) ? responseField.trim() : responseField;
            responseField = responseField.contains(EMPTY_CHAR) ? responseField.substring(0, responseField.indexOf(EMPTY_CHAR)) : responseField;
            responseFields.append(responseField).append(SQL_FIELD_SPLIT_CHAR);
        }
        if (requiredFields.length() >= 1) {
            requiredFieldStr = String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
        }
        if (responseFields.length() >= 1) {
            responseFieldStr = String.valueOf(new StringBuffer(responseFields.substring(0, responseFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
        }
        TableFieldBO tableFieldBO = handleField(apiParamEntityList, requiredFieldStr, responseFieldStr, tableNameInfoBOList);
        createApiInfoBO.setQuerySql(param.getApiGenerateSaveRequest().getSql().replaceAll(MANY_EMPTY_CHAR, EMPTY_CHAR).replaceAll(SQL_END, ""));
        createApiInfoBO.setRequiredFieldStr(tableFieldBO.getRequiredFieldStr());
        createApiInfoBO.setResponseFieldStr(tableFieldBO.getResponseFieldStr());
        return createApiInfoBO;
    }

    private TableFieldBO handleField(List<IcreditApiParamEntity> apiParamEntityList, String requiredFieldStr, String responseFieldStr, List<TableNameInfoBO> tableNameInfoBOList) {
        TableFieldBO tableFieldBO = new TableFieldBO();
        StringBuilder requiredFields = new StringBuilder();
        StringBuilder responseFields = new StringBuilder();
        if(StringUtils.isNotEmpty(requiredFieldStr)) {
            String[] requiredFieldArr = requiredFieldStr.split(SQL_FIELD_SPLIT_CHAR);
            for (IcreditApiParamEntity apiParamEntity : apiParamEntityList) {
                for (String requiredField : requiredFieldArr) {
                    for (TableNameInfoBO tableNameInfoBO : tableNameInfoBOList) {
                        if(StringUtils.isEmpty(tableNameInfoBO.getTableAlias()) && requiredField.equals(apiParamEntity.getFieldName().toLowerCase())){//没有表别名，单表
                            apiParamEntity.setRequired(RequiredFiledEnum.IS_REQUIRED_FIELD.getCode());
                            apiParamEntity.setIsRequest(RequestFiledEnum.IS_REQUEST_FIELD.getCode());
                            requiredFields.append(apiParamEntity.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                            //有表别名，多表
                        }else if (apiParamEntity.getTableName().equals(tableNameInfoBO.getTableName())
                                && requiredField.substring(requiredField.contains(tableNameInfoBO.getTableAlias() + SQL_CONN_CHAR) ? (requiredField.indexOf(SQL_CONN_CHAR) + 1) : 0).equals(apiParamEntity.getFieldName().toLowerCase())) {
                            apiParamEntity.setRequired(RequiredFiledEnum.IS_REQUIRED_FIELD.getCode());
                            apiParamEntity.setIsRequest(RequestFiledEnum.IS_REQUEST_FIELD.getCode());
                            requiredFields.append(tableNameInfoBO.getTableAlias()).append(SQL_CONN_CHAR).append(apiParamEntity.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                        }
                    }
                }
            }
        }
        if(StringUtils.isNotEmpty(responseFieldStr)) {
            String[] responseFieldArr =  responseFieldStr.split(SQL_FIELD_SPLIT_CHAR);
            for (IcreditApiParamEntity apiParamEntity : apiParamEntityList) {
                for (String responseField : responseFieldArr) {
                    for (TableNameInfoBO tableNameInfoBO : tableNameInfoBOList) {
                        if (StringUtils.isEmpty(tableNameInfoBO.getTableAlias()) && responseField.equals(apiParamEntity.getFieldName().toLowerCase())) {//没有表别名，单表
                            apiParamEntity.setIsResponse(ResponseFiledEnum.IS_RESPONSE_FIELD.getCode());
                            responseFields.append(apiParamEntity.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                            //有表别名，多表
                        } else if (apiParamEntity.getTableName().equals(tableNameInfoBO.getTableName())
                                && responseField.substring(responseField.contains(tableNameInfoBO.getTableAlias() + SQL_CONN_CHAR) ? (responseField.indexOf(SQL_CONN_CHAR) + 1) : 0).equals(apiParamEntity.getFieldName().toLowerCase())) {
                            apiParamEntity.setIsResponse(ResponseFiledEnum.IS_RESPONSE_FIELD.getCode());
                            responseFields.append(tableNameInfoBO.getTableAlias()).append(SQL_CONN_CHAR).append(apiParamEntity.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                        }
                    }
                }
            }
        }
        tableFieldBO.setRequiredFieldStr(requiredFields.length() >= 1 ? String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR)))) : null);
        tableFieldBO.setResponseFieldStr(responseFields.length() >= 1 ? String.valueOf(new StringBuffer(responseFields.substring(0, responseFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR)))) : null);
        return tableFieldBO;
    }

    private String getDatasourceInterfaceAddress(IcreditApiBaseEntity apiBaseEntity, List<APIParamResult> params) {
        StringBuilder builder = new StringBuilder(host + "/v" + apiBaseEntity.getApiVersion() + "/" + apiBaseEntity.getPath() + "?");
        for (APIParamResult param : params) {
            //参数拼接like:ID=${ID}
            builder.append(param.getFieldName()).append("=${ ").append(param.getFieldName()).append(" }").append("&");
        }
        String temp = builder.toString();
        String address = temp.substring(0, temp.length() - 1);
        return address;
    }

    private String handleUrl(String url) {
        //根据uri获取jdbc连接
        if(url.contains(SPLIT_URL_FLAG)){//url包含？ -- jdbc:mysql://192.168.0.193:3306/data_source?username=root
            return String.valueOf(new StringBuffer(url.substring(0, url.indexOf(SPLIT_URL_FLAG))).append(SPLIT_URL_FLAG).append(SQL_CHARACTER));
        }else if(url.contains(SEPARATOR)){//url不包含？但包含|  -- jdbc:mysql://192.168.0.193:3306/data_source|username=root
            return String.valueOf(new StringBuffer(url.substring(0, url.indexOf(SEPARATOR))).append(SPLIT_URL_FLAG).append(SQL_CHARACTER));
        }else{//url不包含？和| -- jdbc:mysql://192.168.0.193:3306/daas
            return String.valueOf(new StringBuffer(url).append(SPLIT_URL_FLAG).append(SQL_CHARACTER));
        }
    }

    @Override
    public BusinessResult<List<Map<String, Object>>> getDataSourcesList(DataSourcesListRequest request) {
        return dataSourceFeignClient.getDataSourcesList(request);
    }

    @Override
    public BusinessResult<List<Map<String, String>>> getTableNameList(TableNameListRequest request) {
        return dataSourceFeignClient.getTableNameList(request);
    }

    @Override
    public BusinessResult<List<FieldInfo>> getTableFieldList(TableFieldListRequest request) {
        DatasourceDetailResult datasource = getDatasourceDetail(request.getDatasourceId());
        String uri = datasource.getUri();
        List<FieldInfo> fieldList = new ArrayList<>();
        Connection conn = null;
        try {
            conn = getConnectionByUri(uri);
            ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), PERCENTAGE, request.getTableName(), PERCENTAGE);
            while(rs.next()) {
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setDesc(rs.getString("REMARKS"));
                fieldInfo.setFieldType(rs.getString("TYPE_NAME").toLowerCase());
                fieldInfo.setFieldName(rs.getString("COLUMN_NAME"));
                fieldInfo.setIsResponse(SelectFiledEnum.FIELD_SELECTED.getCode());
                fieldInfo.setIsRequest(SelectFiledEnum.FIELD_NOT_SELECTED.getCode());
                fieldInfo.setRequired(SelectFiledEnum.FIELD_NOT_SELECTED.getCode());
                fieldList.add(fieldInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
        return BusinessResult.success(fieldList);
    }

    private Connection getConnectionByUri(String uri) throws SQLException {
        String username = DBConnectionManager.getInstance().getUsername(uri);
        String password = DBConnectionManager.getInstance().getPassword(uri);
        String url = DBConnectionManager.getInstance().getUri(uri);
        return DriverManager.getConnection(url, username, password);
    }

    private DatasourceDetailResult getDatasourceDetail(String datasourceId) {
        if (StringUtils.isBlank(datasourceId)){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_60000002.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_60000002.getMessage());
        }
        BusinessResult<DatasourceDetailResult> datasourceResult = dataSourceFeignClient.info(datasourceId);
        if (!datasourceResult.isSuccess() || Objects.isNull(datasourceResult.getData())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_60000001.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_60000001.getMessage());
        }
        return datasourceResult.getData();
    }

    @Override
    public BusinessResult<Boolean> checkApiPath(CheckApiPathRequest request) {
        if (!request.getPath().matches("[a-zA-Z]{16}")) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000001.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000001.getMessage());
        }
        IcreditApiBaseEntity apiBaseEntity = apiBaseMapper.findByApiPath(request.getPath());
        if(null != apiBaseEntity && !apiBaseEntity.getId().equals(request.getId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000005.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000005.getMessage());
        }
        return BusinessResult.success(true);
    }

    @Override
    public BusinessResult<Boolean> checkApiName(CheckApiNameRequest request) {
        if (!request.getName().matches("[a-zA-Z0-9\u4e00-\u9fa5_]{2,50}")) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000002.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000002.getMessage());
        }
        IcreditApiBaseEntity apiBaseEntity = apiBaseMapper.findByApiName(request.getName());
        if(null != apiBaseEntity && !apiBaseEntity.getId().equals(request.getId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000003.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000003.getMessage());
        }
        return BusinessResult.success(true);
    }

    @Override
    public Object checkQuerySql(CheckQuerySqlRequest request, String id, Integer apiVersion, Integer type) {
        String sql = request.getSql().replaceAll(MANY_EMPTY_CHAR, EMPTY_CHAR).toLowerCase();
        if(QuerySqlCheckType.NEED_GET_TABLE_FIELD.getCode().equals(type)){
            if(StringUtils.isEmpty(request.getSql())){
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000008.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000008.getMessage());
            }
            if(sql.contains(SQL_SELECT_ALL)){
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000006.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000006.getMessage());
            }
            if(sql.contains(SQL_WHERE) && checkSqlWhere(sql.substring(sql.indexOf(SQL_WHERE) + SQL_WHERE.length()))){
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000041.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000041.getMessage());
            }
        }else{
            if(StringUtils.isEmpty(request.getSql())){
                return ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000008.getMessage();
            }
            if(sql.contains(SQL_SELECT_ALL)){
                return ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000006.getMessage();
            }
            if(sql.contains(SQL_WHERE) && checkSqlWhere(sql.substring(sql.indexOf(SQL_WHERE) + SQL_WHERE.length()))){
                return ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000041.getMessage();
            }
        }
        sql = "explain " + sql.replaceAll("\\$\\{.*?\\}", "''").replaceAll(SQL_END, "");
        DatasourceDetailResult datasource = getDatasourceDetail(request.getDatasourceId());
        String uri = datasource.getUri();
        List<IcreditApiParamEntity> apiParamEntityList = null;
        Connection conn = null;
        CreateApiInfoBO createApiInfoBO = null;
        StringBuilder tableNames = new StringBuilder();
        try {
            conn = getConnectionByUri(uri);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            if(QuerySqlCheckType.NEED_GET_TABLE_FIELD.getCode().equals(type)) {
                String sqlCount = sql.substring(sql.indexOf(SQL_START) + SQL_START.length(), sql.indexOf(SQL_FROM)).replaceAll(EMPTY_CHAR, "");
                apiParamEntityList = new ArrayList<>();
                if(sql.contains(SQL_WHERE)) {
                    if(sqlCount.contains(SQL_COUNT_ONE) || sqlCount.contains(SQL_COUNT_ALL)) {
                        sql = String.valueOf(new StringBuilder(SQL_SELECT_ALL).append(SQL_FROM).append(sql.substring(sql.indexOf(SQL_FROM) + SQL_FROM.length(), sql.indexOf(SQL_WHERE))).append(SQL_LIMIT));
                    }else {
                        sql = String.valueOf(new StringBuilder(sql.substring(sql.indexOf(SQL_START), sql.lastIndexOf(SQL_WHERE))).append(SQL_LIMIT));
                    }
                }else{
                    if(sqlCount.contains(SQL_COUNT_ONE) || sqlCount.contains(SQL_COUNT_ALL)) {
                        sql = String.valueOf(new StringBuilder(SQL_SELECT_ALL).append(SQL_FROM).append(sql.substring(sql.indexOf(SQL_FROM) + SQL_FROM.length())).append(SQL_LIMIT));
                    }else {
                        sql = String.valueOf(new StringBuilder(sql.substring(sql.indexOf(SQL_START))).append(SQL_LIMIT));
                    }
                }
                ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery(sql);
                ResultSetMetaData metaData = rs.getMetaData();
                int len = metaData.getColumnCount();
                List<String> tableNameList = new ArrayList<>();
                for (int i = 1; i <= len; i++) {//获取SQL语句中的表名称
                    if(!tableNameList.contains(metaData.getTableName(i))){
                        tableNameList.add(metaData.getTableName(i));
                        tableNames.append(metaData.getTableName(i)).append(",");
                    }
                }

                DatabaseMetaData databaseMetaData = conn.getMetaData();
                for (String tableName : tableNameList) {//根据表名称获取对应的表字段信息
                    ResultSet columnRs = databaseMetaData.getColumns(conn.getCatalog(), PERCENTAGE, tableName, PERCENTAGE);
                    while (columnRs.next()){
                        IcreditApiParamEntity apiParamEntity = new IcreditApiParamEntity();
                        apiParamEntity.setTableName(tableName);
                        apiParamEntity.setFieldType(columnRs.getString("TYPE_NAME").toLowerCase());
                        apiParamEntity.setFieldName(columnRs.getString("COLUMN_NAME"));
                        apiParamEntity.setDesc(columnRs.getString("REMARKS"));
                        apiParamEntity.setIsRequest(RequestFiledEnum.NOT_IS_REQUEST_FIELD.getCode());
                        apiParamEntity.setIsResponse(ResponseFiledEnum.NOT_IS_RESPONSE_FIELD.getCode());
                        apiParamEntity.setRequired(RequiredFiledEnum.NOT_IS_REQUIRED_FIELD.getCode());
                        apiParamEntity.setApiBaseId(id);
                        apiParamEntity.setApiVersion(apiVersion);
                        apiParamEntityList.add(apiParamEntity);
                    }
                }
                createApiInfoBO = new CreateApiInfoBO();
                createApiInfoBO.setApiParamEntityList(apiParamEntityList);
                createApiInfoBO.setTableNames(String.valueOf(new StringBuffer(tableNames.substring(0, tableNames.lastIndexOf(SQL_FIELD_SPLIT_CHAR)))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000007.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000007.getMessage());
        }finally {
            closeConnection(conn);
        }
        return createApiInfoBO;
    }

    private boolean checkSqlWhere(String sqlWhere) {
        if(sqlWhere.contains(" or ") || sqlWhere.contains(" between")|| sqlWhere.contains(" >")|| sqlWhere.contains(" <") || sqlWhere.contains(" !") || sqlWhere.contains(" like") || sqlWhere.contains(" in") ||
                sqlWhere.contains(" is null") || sqlWhere.contains(" is not null")){
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public BusinessResult<Boolean> publishOrStop(String userId, HiApiPublishRequest request) {
        StringLegalUtils.checkId(request.getApiHiId());
        IcreditApiBaseHiEntity apiBaseHiEntity = apiBaseHiService.getById(request.getApiHiId());
        IcreditApiBaseEntity apiBaseEntity = apiBaseMapper.selectById(apiBaseHiEntity.getApiBaseId());
        if(apiBaseHiEntity.getApiVersion().equals(apiBaseEntity.getApiVersion())){//最新版本
            apiBaseMapper.updatePublishStatusById(apiBaseEntity.getId(), request.getPublishStatus());
        }
        apiBaseHiEntity.setPublishStatus(request.getPublishStatus());
        if(ApiPublishStatusEnum.NO_PUBLISHED.getCode().equals(request.getPublishStatus())){//停止发布
            redisTemplate.delete(String.valueOf(new StringBuilder(apiBaseHiEntity.getPath()).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(apiBaseHiEntity.getApiVersion())));
            apiBaseHiService.saveOrUpdate(apiBaseHiEntity);
        }else if(ApiPublishStatusEnum.PUBLISHED.getCode().equals(request.getPublishStatus())){//发布
            IcreditGenerateApiEntity generateApiEntity = generateApiService.getByApiIdAndVersion(apiBaseEntity.getId(), apiBaseHiEntity.getApiVersion());
            IcreditRegisterApiEntity registerApiEntity = registerApiService.findByApiIdAndApiVersion(apiBaseEntity.getId(), apiBaseHiEntity.getApiVersion());
            if(null == generateApiEntity){//注册api的情况下才会为null
                generateApiEntity = new IcreditGenerateApiEntity();
            }
            if(null == registerApiEntity){//数据源api的情况下才会为null
                registerApiEntity = new IcreditRegisterApiEntity();
            }
            String requiredFieldStr = null;
            String responseFieldStr = null;
            StringBuilder requiredFields = new StringBuilder();
            StringBuilder responseFields = new StringBuilder();
            List<IcreditApiParamEntity> apiParamEntityList = apiParamService.getByApiIdAndVersion(apiBaseEntity.getId(), apiBaseHiEntity.getApiVersion());
            for (IcreditApiParamEntity apiParamEntity : apiParamEntityList) {
                if(RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(apiParamEntity.getRequired())){
                    requiredFields.append(apiParamEntity.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                }
                if(ResponseFiledEnum.IS_RESPONSE_FIELD.getCode().equals(apiParamEntity.getIsResponse())){
                    responseFields.append(apiParamEntity.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                }
            }
            if(requiredFields.length() >= 1) {
                requiredFieldStr = String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
            }
            if(responseFields.length() >= 1) {
                responseFieldStr = String.valueOf(new StringBuffer(responseFields.substring(0, responseFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
            }
            List<RegisterApiParamInfo> registerApiParamInfos = new ArrayList<>();
            if(ApiTypeEnum.API_REGISTER.getCode().equals(apiBaseHiEntity.getType())){//注册api
                registerApiParamInfos = BeanCopyUtils.copy(apiParamEntityList, RegisterApiParamInfo.class);
            }

            if(ApiModelTypeEnum.SQL_CREATE_MODEL.getCode().equals(generateApiEntity.getModel())) {
                String sqlCount = generateApiEntity.getSql().substring(generateApiEntity.getSql().indexOf(SQL_START) + SQL_START.length(), generateApiEntity.getSql().indexOf(SQL_FROM)).replaceAll(" ", "");
                if (sqlCount.contains(SQL_COUNT_ONE) || sqlCount.contains(SQL_COUNT_ALL)) {
                    String responseField = sqlCount.contains(SQL_AS) ? sqlCount.substring(sqlCount.indexOf(SQL_AS) + SQL_AS.length()) : sqlCount.contains(EMPTY_CHAR) ?
                            sqlCount.substring(sqlCount.indexOf(EMPTY_CHAR) + EMPTY_CHAR.length()) : sqlCount;
                    saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseHiEntity.getPath(), apiBaseHiEntity.getName(),
                            generateApiEntity.getModel(), apiBaseHiEntity.getApiVersion(), generateApiEntity.getSql(), requiredFieldStr,
                            responseField, registerApiParamInfos, registerApiEntity.getHost(), registerApiEntity.getPath());
                }
            }else {
                saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseHiEntity.getPath(), apiBaseHiEntity.getName(),
                        generateApiEntity.getModel(), apiBaseHiEntity.getApiVersion(), generateApiEntity.getSql(), requiredFieldStr, responseFieldStr,
                        registerApiParamInfos, registerApiEntity.getHost(), registerApiEntity.getPath());
            }
            apiBaseHiEntity.setPublishUser(userId);
            apiBaseHiEntity.setPublishTime(new Date());
            apiBaseHiService.saveOrUpdate(apiBaseHiEntity);
        }
        return BusinessResult.success(true);
    }

    private void saveApiInfoToRedis(String apiId, String datasourceId, String path, String apiName, Integer apiType, Integer apiVersion, String sql,
                                    String requiredFieldStr, String responseFieldStr, List<RegisterApiParamInfo> registerApiParamInfos, String reqHost, String reqPath) {
        BusinessResult<ConnectionInfoVO> connResult = dataSourceFeignClient.getConnectionInfo(new DataSourceInfoRequest(datasourceId));
        ConnectionInfoVO connInfo = connResult.getData();
        RedisApiInfo redisApiInfo = new RedisApiInfo();
        redisApiInfo.setApiId(apiId);
        redisApiInfo.setApiType(apiType);
        redisApiInfo.setApiName(apiName);
        if(null != connInfo){
            redisApiInfo.setUrl(handleUrl(connInfo.getUrl()));
            redisApiInfo.setUserName(connInfo.getUsername());
            redisApiInfo.setPassword(connInfo.getPassword());
            redisApiInfo.setQuerySql(sql);
        }
        redisApiInfo.setRequiredFields(requiredFieldStr);
        redisApiInfo.setResponseFields(responseFieldStr);
        redisApiInfo.setRegisterApiParamInfoList(registerApiParamInfos);
        redisApiInfo.setReqHost(reqHost);
        redisApiInfo.setReqPath(reqPath);
        redisTemplate.opsForValue().set(String.valueOf(new StringBuilder(path).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(apiVersion)), JSON.toJSONString(redisApiInfo));
    }

    @Override
    public BusinessResult<List<ApiNameAndIdListResult>> getApiByApiGroupId(ApiNameAndIdListRequest request) {
        return BusinessResult.success(apiBaseMapper.getApiByApiGroupId(request.getApiGroupIds()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<ApiSaveResult> createAndPublish(String userId, DatasourceApiSaveParam param) {
        long startTime = System.currentTimeMillis();
        IcreditApiBaseEntity apiBaseEntity = apiBaseMapper.findByApiPath(param.getPath());
        log.info("api查询耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
        startTime = System.currentTimeMillis();
        if (!Objects.isNull(apiBaseEntity)){
            param.setId(apiBaseEntity.getId());
        }else {
            apiBaseEntity = new IcreditApiBaseEntity();
            checkApiName(new CheckApiNameRequest(param.getId(), param.getName()));
            checkApiPath(new CheckApiPathRequest(param.getId(), param.getPath()));
        }
        log.info("参数校验查询耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
        //保存api基础信息
        startTime = System.currentTimeMillis();
        BeanUtils.copyProperties(param, apiBaseEntity);
        apiBaseEntity.setInterfaceSource(InterfaceSourceEnum.OUT_SIDE.getCode());
        //TODO:这个版本没有版本号，直接写死
        apiBaseEntity.setApiVersion(1);
        //默认分组id
        apiBaseEntity.setApiGroupId(DEFAULT_API_GROUP);
        if (ApiSaveStatusEnum.API_SAVE.getCode().equals(param.getSaveType())) {//保存
            apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.WAIT_PUBLISH.getCode());
        } else {
            apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
            apiBaseEntity.setPublishUser(userId);
            apiBaseEntity.setPublishTime(new Date());
        }
        saveOrUpdate(apiBaseEntity);
        log.info("保存api耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
        startTime = System.currentTimeMillis();
        String querySql;
        String requiredFieldStr = null;
        String responseFieldStr = null;
        CreateApiInfoBO sqlModelInfo = new CreateApiInfoBO();
        StringBuffer requiredFields = new StringBuffer();//请求参数
        StringBuffer responseFields = new StringBuffer();//返回参数
        List<IcreditApiParamEntity> apiParamEntityList = new ArrayList<>();
        startTime = System.currentTimeMillis();
        apiParamService.removeByApiIdAndApiVersion(apiBaseEntity.getId(), apiBaseEntity.getApiVersion());
        log.info("移除apiParam耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
        if (ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())) {//表单生成模式
            boolean isHaveRespField = false;
            StringBuffer querySqlPrefix = new StringBuffer(SQL_START);
            StringBuffer querySqlSuffix = new StringBuffer(SQL_WHERE);
            //保存 api param
            for (DatasourceApiParamSaveRequest datasourceApiParamSaveRequest : param.getApiParamSaveRequestList()) {
                IcreditApiParamEntity apiParamEntity = new IcreditApiParamEntity();
                apiParamEntity.setApiBaseId(apiBaseEntity.getId());
                apiParamEntity.setApiVersion(apiBaseEntity.getApiVersion());
                apiParamEntity.setDesc(datasourceApiParamSaveRequest.getDesc());
                apiParamEntity.setTableName(param.getApiGenerateSaveRequest().getTableName());
                apiParamEntity.setRequired(datasourceApiParamSaveRequest.getRequired());
                apiParamEntity.setFieldName(datasourceApiParamSaveRequest.getFieldName());
                apiParamEntity.setFieldType(datasourceApiParamSaveRequest.getFieldType());
                apiParamEntity.setIsRequest(datasourceApiParamSaveRequest.getIsRequest());
                apiParamEntity.setIsResponse(datasourceApiParamSaveRequest.getIsResponse());
                apiParamEntityList.add(apiParamEntity);
                if (ResponseFiledEnum.IS_RESPONSE_FIELD.getCode().equals(datasourceApiParamSaveRequest.getIsResponse())) {
                    querySqlPrefix.append(datasourceApiParamSaveRequest.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                    responseFields.append(datasourceApiParamSaveRequest.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                    isHaveRespField = true;
                }
                if (RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(datasourceApiParamSaveRequest.getIsRequest())) {
                    querySqlSuffix.append(datasourceApiParamSaveRequest.getFieldName())
                            .append(" = ${").append(datasourceApiParamSaveRequest.getFieldName()).append("}").append(SQL_AND);
                }
                if (RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(datasourceApiParamSaveRequest.getRequired())) {
                    requiredFields.append(datasourceApiParamSaveRequest.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                }
            }
            if (!isHaveRespField) {//没有勾选返回参数
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
            }
            querySql = String.valueOf(new StringBuffer(querySqlPrefix.substring(0, querySqlPrefix.lastIndexOf(SQL_FIELD_SPLIT_CHAR)))
                    .append(SQL_FROM).append(param.getApiGenerateSaveRequest().getTableName()).append(querySqlSuffix));
            if (querySql.endsWith(SQL_WHERE)) {
                querySql = querySql.substring(0, querySql.lastIndexOf(SQL_WHERE));
            }
            if (querySql.endsWith(SQL_AND)) {
                querySql = querySql.substring(0, querySql.lastIndexOf(SQL_AND));
            }
            if (requiredFields.length() >= 1) {
                requiredFieldStr = String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
            }
            if (responseFields.length() >= 1) {
                responseFieldStr = String.valueOf(new StringBuffer(responseFields.substring(0, responseFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
            }
        } else {
            sqlModelInfo = (CreateApiInfoBO) checkQuerySql(new CheckQuerySqlRequest(param.getApiGenerateSaveRequest().getDatasourceId(), param.getApiGenerateSaveRequest().getSql()), apiBaseEntity.getId(), apiBaseEntity.getApiVersion(), QuerySqlCheckType.NEED_GET_TABLE_FIELD.getCode());
            apiParamEntityList = sqlModelInfo.getApiParamEntityList();
            sqlModelInfo = (CreateApiInfoBO) checkQuerySql(new CheckQuerySqlRequest(param.getApiGenerateSaveRequest().getDatasourceId(), param.getApiGenerateSaveRequest().getSql()), apiBaseEntity.getId(), apiBaseEntity.getApiVersion(), QuerySqlCheckType.NEED_GET_TABLE_FIELD.getCode());
            querySql = param.getApiGenerateSaveRequest().getSql().replaceAll(MANY_EMPTY_CHAR, EMPTY_CHAR).toLowerCase().replaceAll(SQL_END, "");
            if(null != sqlModelInfo) {
                apiParamEntityList = sqlModelInfo.getApiParamEntityList();
                String[] tableNames = null;
                String[] responseFieldArr = querySql.substring(SQL_START.length(), querySql.indexOf(SQL_FROM)).split(SQL_FIELD_SPLIT_CHAR);
                String[] requiredFieldArr;
                if (querySql.contains(SQL_WHERE)) {
                    requiredFieldArr = querySql.substring(querySql.indexOf(SQL_WHERE) + SQL_WHERE.length()).split(SQL_AND);
                    tableNames = querySql.substring(querySql.indexOf(SQL_FROM) + SQL_FROM.length(), querySql.indexOf(SQL_WHERE)).replaceAll(" left join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" right join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" full join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" inner join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" join ", SQL_FIELD_SPLIT_CHAR).split(SQL_FIELD_SPLIT_CHAR);
                } else {
                    requiredFieldArr = new String[]{};
                    tableNames = querySql.substring(querySql.indexOf(SQL_FROM) + SQL_FROM.length()).replaceAll(" left join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" right join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" full join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" inner join ", SQL_FIELD_SPLIT_CHAR).replaceAll(" join ", SQL_FIELD_SPLIT_CHAR).split(SQL_FIELD_SPLIT_CHAR);
                }
                for (String requiredField : requiredFieldArr) {
                    if (requiredField.contains("${")) {
                        requiredFields.append(requiredField.substring(0, requiredField.indexOf(" ="))).append(SQL_FIELD_SPLIT_CHAR);
                    }
                }
                List<TableNameInfoBO> tableNameInfoBOList = new ArrayList<>(tableNames.length);
                for (String tableName : tableNames) {
                    TableNameInfoBO tableNameInfoBO = new TableNameInfoBO();
                    tableName = tableName.contains(SQL_ON) ? tableName.substring(0, tableName.indexOf(SQL_ON)) : tableName;
                    tableName = tableName.startsWith(EMPTY_CHAR) ? tableName.trim() : tableName;
                    String tableAlia = tableName.contains(SQL_AS) ? tableName.substring(tableName.indexOf(SQL_AS) + SQL_AS.length()) : tableName.contains(EMPTY_CHAR) ? tableName.substring(tableName.indexOf(EMPTY_CHAR) + 1) : "";
                    tableNameInfoBO.setTableAlias(tableAlia.replaceAll(EMPTY_CHAR, ""));
                    tableNameInfoBO.setTableName(tableName.contains(EMPTY_CHAR) ? tableName.substring(0, tableName.indexOf(EMPTY_CHAR)) : tableName);
                    tableNameInfoBOList.add(tableNameInfoBO);
                }
                for (String responseField : responseFieldArr) {
                    responseField = responseField.startsWith(EMPTY_CHAR) ? responseField.trim() : responseField;
                    responseField = responseField.contains(EMPTY_CHAR) ? responseField.substring(0, responseField.indexOf(EMPTY_CHAR)) : responseField;
                    responseFields.append(responseField).append(SQL_FIELD_SPLIT_CHAR);
                }
                if (requiredFields.length() >= 1) {
                    requiredFieldStr = String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
                }
                if (responseFields.length() >= 1) {
                    responseFieldStr = String.valueOf(new StringBuffer(responseFields.substring(0, responseFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
                }
                handleField(apiParamEntityList, requiredFieldStr, responseFieldStr, tableNameInfoBOList);
            }else{
                String sqlCount = querySql.substring(querySql.indexOf(SQL_START) + SQL_START.length(), querySql.indexOf(SQL_FROM));
                responseFieldStr = sqlCount.contains(SQL_AS) ? sqlCount.substring(sqlCount.indexOf(SQL_AS) + SQL_AS.length()) : sqlCount.contains(EMPTY_CHAR) ? sqlCount.substring(sqlCount.indexOf(EMPTY_CHAR) + EMPTY_CHAR.length()) : sqlCount;
            }
        }
        if(!CollectionUtils.isEmpty(apiParamEntityList)) {
            apiParamService.saveOrUpdateBatch(apiParamEntityList);
        }

        log.info("保存apiParam耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
        //保存 generate api
        startTime = System.currentTimeMillis();
        generateApiService.removeByApiIdAndApiVersion(apiBaseEntity.getId(), apiBaseEntity.getApiVersion());
        IcreditGenerateApiEntity generateApiEntity = new IcreditGenerateApiEntity();
        BeanUtils.copyProperties(param.getApiGenerateSaveRequest(), generateApiEntity);
        generateApiEntity.setId(param.getApiGenerateSaveRequest().getId());
        generateApiEntity.setApiBaseId(apiBaseEntity.getId());
        generateApiEntity.setApiVersion(apiBaseEntity.getApiVersion());
        if (ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())) {
            generateApiEntity.setSql(querySql);
        }
        if (null != sqlModelInfo && ApiModelTypeEnum.SQL_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())) {
            generateApiEntity.setTableName(sqlModelInfo.getTableNames());
        }
        generateApiService.saveOrUpdate(generateApiEntity);
        log.info("保存generateApi耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");

        //发布操作 存放信息到redis
        startTime = System.currentTimeMillis();
        if (ApiSaveStatusEnum.API_PUBLISH.getCode().equals(param.getSaveType())){
            List<RegisterApiParamInfo> registerApiParamInfos = new ArrayList<>();
            if(ApiTypeEnum.API_REGISTER.getCode().equals(apiBaseEntity.getType())){//注册api
                BeanUtils.copyProperties(apiParamEntityList, registerApiParamInfos);
            }
            saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseEntity.getPath(), apiBaseEntity.getName(),
                    apiBaseEntity.getType(), apiBaseEntity.getApiVersion(), querySql, requiredFieldStr, responseFieldStr, registerApiParamInfos,
                    null, null);
        }
        log.info("发布耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
        //返回参数
        ApiSaveResult apiSaveResult = new ApiSaveResult();
        apiSaveResult.setId(apiBaseEntity.getId());
        ApiGenerateSaveResult generateApiSaveResult = new ApiGenerateSaveResult();
        BeanUtils.copyProperties(generateApiEntity, generateApiSaveResult);
        apiSaveResult.setApiGenerateSaveRequest(generateApiSaveResult);
        startTime = System.currentTimeMillis();
        List<ApiParamSaveResult> apiParamSaveResultList = BeanCopyUtils.copy(apiParamEntityList, ApiParamSaveResult.class);
        apiSaveResult.setApiParamSaveRequestList(apiParamSaveResultList);
        apiBaseEntity = publish(userId, new ApiPublishRequest(apiBaseEntity.getId(), ApiPublishStatusEnum.PUBLISHED.getCode()));
        IcreditApiBaseHiEntity apiBaseHiEntity = BeanCopyUtils.copyProperties(apiBaseEntity, new IcreditApiBaseHiEntity());
        apiBaseHiEntity.setId(null);
        apiBaseHiEntity.setApiBaseId(apiBaseEntity.getId());
        //先删除，再插入
        apiBaseHiService.removeByApiBaseId(apiBaseEntity.getId());
        apiBaseHiService.save(apiBaseHiEntity);
        //只对入参做筛选
        apiParamEntityList = apiParamEntityList.stream()
                .filter((IcreditApiParamEntity a) -> RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(a.getIsRequest()))
                .collect(Collectors.toList());
        List<APIParamResult> apiParamList = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.copy(apiParamEntityList, APIParamResult.class);
        apiSaveResult.setDesc(getDatasourceInterfaceAddress(apiBaseEntity, apiParamList));
        log.info("组合返回参数耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
        return BusinessResult.success(apiSaveResult);
    }

    private IcreditApiBaseEntity publish(String userId, ApiPublishRequest request) {
        long startTime = System.currentTimeMillis();
        apiBaseMapper.updatePublishStatusById(request.getId(), request.getPublishStatus());
        IcreditApiBaseEntity apiBaseEntity = apiBaseMapper.selectById(request.getId());
        if(ApiPublishStatusEnum.NO_PUBLISHED.getCode().equals(request.getPublishStatus())){//停止发布
            redisTemplate.delete(String.valueOf(new StringBuilder(apiBaseEntity.getPath()).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(apiBaseEntity.getApiVersion())));
        }else if(ApiPublishStatusEnum.PUBLISHED.getCode().equals(request.getPublishStatus())){//发布
            IcreditGenerateApiEntity generateApiEntity = generateApiService.getByApiIdAndVersion(apiBaseEntity.getId(), apiBaseEntity.getApiVersion());

            String requiredFieldStr = null;
            String responseFieldStr = null;
            StringBuilder requiredFields = new StringBuilder();
            StringBuilder responseFields = new StringBuilder();
            List<IcreditApiParamEntity> apiParamEntityList = apiParamService.getByApiIdAndVersion(apiBaseEntity.getId(), apiBaseEntity.getApiVersion());
            for (IcreditApiParamEntity apiParamEntity : apiParamEntityList) {
                if(RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(apiParamEntity.getRequired())){
                    requiredFields.append(apiParamEntity.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                }
                if(ResponseFiledEnum.IS_RESPONSE_FIELD.getCode().equals(apiParamEntity.getIsResponse())){
                    responseFields.append(apiParamEntity.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                }
            }
            if(requiredFields.length() >= 1) {
                requiredFieldStr = String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
            }
            if(responseFields.length() >= 1) {
                responseFieldStr = String.valueOf(new StringBuffer(responseFields.substring(0, responseFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
            }
            List<RegisterApiParamInfo> registerApiParamInfos = new ArrayList<>();
            if(ApiTypeEnum.API_REGISTER.getCode().equals(apiBaseEntity.getType())){//注册api
                BeanUtils.copyProperties(apiParamEntityList, registerApiParamInfos);
            }
            saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseEntity.getPath(), apiBaseEntity.getName(),
                    apiBaseEntity.getType(), apiBaseEntity.getApiVersion(), generateApiEntity.getSql(), requiredFieldStr, responseFieldStr,
                    registerApiParamInfos, null, null);
            apiBaseEntity.setPublishUser(userId);
            apiBaseEntity.setPublishTime(new Date());
            saveOrUpdate(apiBaseEntity);
        }
        log.info("发布耗时:{}毫秒", System.currentTimeMillis() - startTime);
        return apiBaseEntity;
    }


    @Override
    public String findPublishedByWorkFlowId(String workFlowId) {
        return apiBaseMapper.findPublishedByWorkFlowId(workFlowId);
    }

    @Override
    public List<String> getIdsByApiGroupIds(List<String> apiGroupIdList) {
        return apiBaseMapper.getIdsByApiGroupIds(apiGroupIdList);
    }

    @Override
    public String findPublishedByApiGroupId(String apiGroupId) {
        return apiBaseMapper.findPublishedByApiGroupId(apiGroupId);
    }

    @Override
    public void truthDelById(String id) {
        apiBaseMapper.truthDelById(id);
    }
}
