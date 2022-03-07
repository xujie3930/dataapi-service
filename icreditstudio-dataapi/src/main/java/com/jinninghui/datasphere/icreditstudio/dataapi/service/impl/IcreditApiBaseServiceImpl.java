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
import com.jinninghui.datasphere.icreditstudio.dataapi.service.factory.ApiBaseFactory;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.DBConnectionManager;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.*;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.Query;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.DateUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.sm4.SM4Utils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * <p>
 * API表基础信息表 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditApiBaseServiceImpl extends ServiceImpl<IcreditApiBaseMapper, IcreditApiBaseEntity> implements IcreditApiBaseService {

    @Resource
    private IcreditGenerateApiService generateApiService;
    @Resource
    private IcreditApiParamService apiParamService;
    @Autowired
    private IcreditApiGroupService apiGroupService;
    @Autowired
    private IcreditWorkFlowService workFlowService;
    @Resource
    private DatasourceFeignClient dataSourceFeignClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private IcreditApiBaseMapper apiBaseMapper;
    @Autowired
    private ApiBaseFactory apiBaseFactory;

    private static final String EMPTY_CHAR = " ";
    private static final String MANY_EMPTY_CHAR = " +";
    private static final String SQL_START = "select ";
    private static final String SQL_AND = " and ";
    private static final String SQL_FIELD_SPLIT_CHAR = ",";
    private static final String SQL_WHERE = " where ";
    private static final String SQL_FROM = " from ";
    private static final String SEPARATOR = "|";
    private static final String SPLIT_URL_FLAG = "?";
    private static final String SQL_CHARACTER = "useSSL=false&useUnicode=true&characterEncoding=utf8";
    private static final String REDIS_KEY_SPLIT_JOINT_CHAR = ":";

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
    public BusinessResult<ApiSaveResult> createDataSourceApi(String userId, DatasourceApiSaveParam param) {
        checkApiName(new CheckApiNameRequest(param.getId(), param.getName()));
        checkApiPath(new CheckApiPathRequest(param.getId(), param.getPath()));
        //保存api基础信息
        IcreditApiBaseEntity apiBaseEntity = new IcreditApiBaseEntity();
        BeanUtils.copyProperties(param, apiBaseEntity);
        apiBaseEntity.setApiVersion(1);
        if (ApiSaveStatusEnum.API_SAVE.getCode().equals(param.getSaveType())) {//保存
            apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.WAIT_PUBLISH.getCode());
        } else {
            apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
            apiBaseEntity.setPublishUser(userId);
            apiBaseEntity.setPublishTime(new Date());
        }
        saveOrUpdate(apiBaseEntity);

        String querySql;
        String requiredFieldStr = null;
        String responseFieldStr = null;
        StringBuffer requiredFields = new StringBuffer();//请求参数
        StringBuffer responseFields = new StringBuffer();//返回参数
        List<IcreditApiParamEntity> apiParamEntityList = new ArrayList<>();
        apiParamService.removeByApiId(apiBaseEntity.getId());
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
            apiParamEntityList = checkQuerySql(new CheckQuerySqlRequest(param.getApiGenerateSaveRequest().getDatasourceId(), param.getApiGenerateSaveRequest().getSql()), apiBaseEntity.getId(), apiBaseEntity.getApiVersion(), QuerySqlCheckType.NEED_GET_TABLE_FIELD.getCode());
            querySql = param.getApiGenerateSaveRequest().getSql().replaceAll(MANY_EMPTY_CHAR, EMPTY_CHAR).toLowerCase();
            String[] responseFieldArr = querySql.substring(SQL_START.length(), querySql.indexOf(SQL_FROM)).split(SQL_FIELD_SPLIT_CHAR);
            String[] requiredFieldArr = querySql.substring(querySql.indexOf(SQL_WHERE) + SQL_WHERE.length()).split(SQL_AND);
            for (String requiredField : requiredFieldArr) {
                requiredFields.append(requiredField.substring(0, requiredField.indexOf(" ="))).append(SQL_FIELD_SPLIT_CHAR);
            }
            for (String responseField : responseFieldArr) {
                responseFields.append(responseField.substring(0, responseField.indexOf(EMPTY_CHAR))).append(SQL_FIELD_SPLIT_CHAR);
            }
            if (requiredFields.length() >= 1) {
                requiredFieldStr = String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
            }
            if (responseFields.length() >= 1) {
                responseFieldStr = String.valueOf(new StringBuffer(responseFields.substring(0, responseFields.lastIndexOf(SQL_FIELD_SPLIT_CHAR))));
            }
            handleField(apiParamEntityList, requiredFieldStr, responseFieldStr);
        }
        apiParamService.saveOrUpdateBatch(apiParamEntityList);

        //保存 generate api
        IcreditGenerateApiEntity generateApiEntity = new IcreditGenerateApiEntity();
        BeanUtils.copyProperties(param.getApiGenerateSaveRequest(), generateApiEntity);
        generateApiEntity.setId(param.getApiGenerateSaveRequest().getId());
        generateApiEntity.setApiBaseId(apiBaseEntity.getId());
        generateApiEntity.setApiVersion(apiBaseEntity.getApiVersion());
        if (ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())) {
            generateApiEntity.setSql(querySql);
        }
        generateApiService.saveOrUpdate(generateApiEntity);

        //发布操作 存放信息到redis
        if (ApiSaveStatusEnum.API_PUBLISH.getCode().equals(param.getSaveType())){
            saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseEntity.getPath(), apiBaseEntity.getName(), generateApiEntity.getModel(), apiBaseEntity.getApiVersion(), querySql, requiredFieldStr, responseFieldStr);
        }
        //返回参数
        ApiSaveResult apiSaveResult = new ApiSaveResult();
        apiSaveResult.setId(apiBaseEntity.getId());
        ApiGenerateSaveResult generateApiSaveResult = new ApiGenerateSaveResult();
        BeanUtils.copyProperties(generateApiEntity, generateApiSaveResult);
        apiSaveResult.setApiGenerateSaveRequest(generateApiSaveResult);
        List<ApiParamSaveResult> apiParamSaveResultList = BeanCopyUtils.copy(apiParamEntityList, ApiParamSaveResult.class);
        apiSaveResult.setApiParamSaveRequestList(apiParamSaveResultList);
        return BusinessResult.success(apiSaveResult);
    }

    private void handleField(List<IcreditApiParamEntity> apiParamEntityList, String requiredFieldStr, String responseFieldStr) {
        String[] requiredFieldArr =  requiredFieldStr.split(SQL_FIELD_SPLIT_CHAR);
        String[] responseFieldArr =  responseFieldStr.split(SQL_FIELD_SPLIT_CHAR);
        for (IcreditApiParamEntity apiParamEntity : apiParamEntityList) {
            for (String requiredField : requiredFieldArr) {
                if(requiredField.equals(apiParamEntity.getFieldName())){
                    apiParamEntity.setRequired(RequiredFiledEnum.IS_REQUIRED_FIELD.getCode());
                    apiParamEntity.setIsRequest(RequestFiledEnum.IS_REQUEST_FIELD.getCode());
                }
            }
        }
        for (IcreditApiParamEntity apiParamEntity : apiParamEntityList) {
            for (String responseField : responseFieldArr) {
                if(responseField.equals(apiParamEntity.getFieldName())){
                    apiParamEntity.setIsResponse(ResponseFiledEnum.IS_RESPONSE_FIELD.getCode());
                }
            }
        }
    }

    @Override
    @ResultReturning
    public BusinessResult<ApiDetailResult> detail(String id) {
        ApiDetailResult result = new ApiDetailResult();
        IcreditApiBaseEntity apiBaseEntity = getById(id);
        if (Objects.isNull(apiBaseEntity)) {
            return BusinessResult.success(result);
        }
        BeanCopyUtils.copyProperties(apiBaseEntity, result);
        result.setApiPath(apiBaseEntity.getPath());
        //根据不同API类型返回不同对象
        ApiBaseService apiService = apiBaseFactory.getApiService(apiBaseEntity.getType());
        apiService.setApiBaseResult(result);
        //获取其业务流程和分组名称
        IcreditApiGroupEntity apiGroupEntity = apiGroupService.getById(apiBaseEntity.getApiGroupId());
        if (!Objects.isNull(apiGroupEntity)) {
            IcreditWorkFlowEntity workFlowEntity = workFlowService.getById(apiGroupEntity.getWorkId());
            if (!Objects.isNull(workFlowEntity)) {
                //目标文件夹=业务流程/分组
                result.setDestination(workFlowEntity.getName() + "/" + apiGroupEntity.getName());
            }
        }
        //获取其param参数
        List<IcreditApiParamEntity> apiParamEntityList = apiParamService.getByApiBaseId(id);
        List<APIParamResult> apiParamList = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.copy(apiParamEntityList, APIParamResult.class);
        result.setParamList(apiParamList);
        result.setCreateTime(Optional.ofNullable(apiBaseEntity.getCreateTime()).orElse(new Date()).getTime());
        result.setPublishTime(Optional.ofNullable(apiBaseEntity.getPublishTime()).orElse(new Date()).getTime());
        return BusinessResult.success(result);
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
        Connection conn = getConnectionByUri(uri, datasource.getType());
        List<FieldInfo> fieldList = new ArrayList<>();
        try {
            ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), "%", request.getTableName(), "%");
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
            DBConnectionManager.getInstance().freeConnection(uri, conn);
        }
        return BusinessResult.success(fieldList);
    }

    private Connection getConnectionByUri(String uri, Integer datasourceType) {
        String username = getUsername(uri);
        String password = getPassword(uri);
        String url = getUri(uri);
        return DBConnectionManager.getInstance().getConnection(url, username, password, datasourceType);
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
        if (!request.getName().matches("[a-zA-Z0-9\u4e00-\u9fa5_]{1,50}")) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000002.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000002.getMessage());
        }
        IcreditApiBaseEntity apiBaseEntity = apiBaseMapper.findByApiName(request.getName());
        if(null != apiBaseEntity && !apiBaseEntity.getId().equals(request.getId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000003.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000003.getMessage());
        }
        return BusinessResult.success(true);
    }

    @Override
    public List<IcreditApiParamEntity> checkQuerySql(CheckQuerySqlRequest request, String id, Integer apiVersion, Integer type) {
        if(StringUtils.isEmpty(request.getSql())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000008.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000008.getMessage());
        }
        String sql = request.getSql().replaceAll(MANY_EMPTY_CHAR, EMPTY_CHAR).toLowerCase();
        if(sql.contains("select *")){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000006.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000006.getMessage());
        }
        sql = "explain " + sql.replaceAll("\\$\\{.*?\\}", "''");
        DatasourceDetailResult datasource = getDatasourceDetail(request.getDatasourceId());
        String uri = datasource.getUri();
        Connection conn = getConnectionByUri(uri, datasource.getType());
        List<IcreditApiParamEntity> apiParamEntityList = null;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            if(QuerySqlCheckType.NEED_GET_TABLE_FIELD.getCode().equals(type)) {
                apiParamEntityList = new ArrayList<>();
                sql = String.valueOf(new StringBuilder(sql.substring(sql.indexOf(SQL_START), sql.lastIndexOf(SQL_WHERE))).append(" limit 1"));
                ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery(sql);
                ResultSetMetaData metaData = rs.getMetaData();
                int len = metaData.getColumnCount();
                List<String> tableNameList = new ArrayList<>();
                for (int i = 1; i <= len; i++) {//获取SQL语句中的表名称
                    if(!tableNameList.contains(metaData.getTableName(i))){
                        tableNameList.add(metaData.getTableName(i));
                    }
                }

                DatabaseMetaData databaseMetaData = conn.getMetaData();
                for (String tableName : tableNameList) {//根据表名称获取对应的表字段信息
                    ResultSet columnRs = databaseMetaData.getColumns(null, "%", tableName, "%");
                    while (columnRs.next()){
                        IcreditApiParamEntity apiParamEntity = new IcreditApiParamEntity();
                        apiParamEntity.setTableName(tableName);
                        apiParamEntity.setFieldType(columnRs.getString("TYPE_NAME").toLowerCase());
                        apiParamEntity.setFieldName(columnRs.getString("COLUMN_NAME"));
                        //todo 字段中文描述
                        apiParamEntity.setDesc(columnRs.getString("REMARKS"));
                        apiParamEntity.setIsRequest(RequestFiledEnum.NOT_IS_REQUEST_FIELD.getCode());
                        apiParamEntity.setIsResponse(ResponseFiledEnum.NOT_IS_RESPONSE_FIELD.getCode());
                        apiParamEntity.setRequired(RequiredFiledEnum.NOT_IS_REQUIRED_FIELD.getCode());
                        apiParamEntity.setApiBaseId(id);
                        apiParamEntity.setApiVersion(apiVersion);
                        apiParamEntityList.add(apiParamEntity);
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000007.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000007.getMessage());
        }finally {
            DBConnectionManager.getInstance().freeConnection(uri, conn);
        }
        return apiParamEntityList;
    }

    @Override
    @Transactional
    public BusinessResult<Boolean> publishOrStop(String userId, ApiPublishRequest request) {
        apiBaseMapper.updatePublishStatusById(request.getId(), request.getPublishStatus());
        IcreditApiBaseEntity apiBaseEntity = apiBaseMapper.selectById(request.getId());
        if(StringUtils.isEmpty(apiBaseEntity.getPublishUser())){
            apiBaseEntity.setPublishUser(userId);
            apiBaseEntity.setPublishTime(new Date());
            saveOrUpdate(apiBaseEntity);
        }
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
            saveApiInfoToRedis(apiBaseEntity.getId(), generateApiEntity.getDatasourceId(), apiBaseEntity.getPath(), apiBaseEntity.getName(), generateApiEntity.getModel(), apiBaseEntity.getApiVersion(), generateApiEntity.getSql(), requiredFieldStr, responseFieldStr);
        }
        return BusinessResult.success(true);
    }

    private void saveApiInfoToRedis(String apiId, String datasourceId, String path, String apiName, Integer apiType, Integer apiVersion, String sql, String requiredFieldStr, String responseFieldStr) {
        BusinessResult<ConnectionInfoVO> connResult = dataSourceFeignClient.getConnectionInfo(new DataSourceInfoRequest(datasourceId));
        ConnectionInfoVO connInfo = connResult.getData();
        RedisApiInfo redisApiInfo = new RedisApiInfo();
        redisApiInfo.setApiId(apiId);
        redisApiInfo.setApiType(apiType);
        redisApiInfo.setApiName(apiName);
        redisApiInfo.setUrl(handleUrl(connInfo.getUrl()));
        redisApiInfo.setUserName(connInfo.getUsername());
        redisApiInfo.setPassword(connInfo.getPassword());
        redisApiInfo.setQuerySql(sql);
        redisApiInfo.setRequiredFields(requiredFieldStr);
        redisApiInfo.setResponseFields(responseFieldStr);
        redisTemplate.opsForValue().set(String.valueOf(new StringBuilder(path).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(apiVersion)), JSON.toJSONString(redisApiInfo));
    }

    @Override
    public BusinessResult<List<ApiNameAndIdListResult>> getApiByApiGroupId(ApiNameAndIdListRequest request) {
        return BusinessResult.success(apiBaseMapper.getApiByApiGroupId(request.getApiGroupIds()));
    }

    private String getUsername(String uri) {
        //根据uri获取username
        String temp = uri.substring(uri.indexOf("username=") + "username=".length());
        String username = temp.substring(0, temp.indexOf(SEPARATOR));
        return username;
    }

    private String getPassword(String uri) {
        //根据uri获取password
        String temp = uri.substring(uri.indexOf("password=") + "password=".length());
        String password;
        if (!temp.endsWith(SEPARATOR)) {
            password = temp;
        } else {
            password = temp.substring(0, temp.indexOf(SEPARATOR));
        }
        SM4Utils sm4 = new SM4Utils();
        return sm4.decryptData_ECB(password);
    }

    private String getUri(String uri) {
        //根据uri获取jdbc连接
        if(uri.contains(SPLIT_URL_FLAG)){//url包含？ -- jdbc:mysql://192.168.0.193:3306/data_source?username=root
            return String.valueOf(new StringBuffer(uri.substring(0, uri.indexOf(SPLIT_URL_FLAG))).append(SPLIT_URL_FLAG).append(SQL_CHARACTER));
        }else if(uri.contains(SEPARATOR)){//url不包含？但包含|  -- jdbc:mysql://192.168.0.193:3306/data_source|username=root
            return String.valueOf(new StringBuffer(uri.substring(0, uri.indexOf(SEPARATOR))).append(SPLIT_URL_FLAG).append(SQL_CHARACTER));
        }else{//url不包含？和| -- jdbc:mysql://192.168.0.193:3306/daas
            return String.valueOf(new StringBuffer(uri).append(SPLIT_URL_FLAG).append(SQL_CHARACTER));
        }
    }
}
