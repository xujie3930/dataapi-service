package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.FieldInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisInterfaceInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiParamEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditGenerateApiEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.DatasourceFeignClient;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DatasourceDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.DBConnectionManager;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DataSourceInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.ConnectionInfoVO;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiParamService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditGenerateApiService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiBaseResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiParamSaveResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiSaveResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGenerateSaveResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.Query;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    @Resource
    private DatasourceFeignClient dataSourceFeignClient;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private IcreditApiBaseMapper apiBaseMapper;

    private static final String SQL_AND = " AND ";
    private static final String SQL_FIELD_SPLIT_CHAR = ",";
    private static final String SQL_WHERE = " WHERE ";
    private static final String SEPARATOR = "|";
    private static final String SPLIT_URL_FLAG = "?";
    private static final String SQL_CHARACTER = "useSSL=false&useUnicode=true&characterEncoding=utf8";

    @Override
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
            wrapper.eq(IcreditApiBaseEntity.PUBLISH_STATUS, request.getPublishTimeStart());
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
    @Transactional
    public BusinessResult<ApiSaveResult> createDataSourceApi(String userId, DatasourceApiSaveParam param) {
        checkApiName(new CheckApiNameRequest(param.getId(), param.getName()));
        checkApiPath(new CheckApiPathRequest(param.getId(), param.getPath()));
        //保存api基础信息
        IcreditApiBaseEntity apiBaseEntity = new IcreditApiBaseEntity();
        BeanUtils.copyProperties(param, apiBaseEntity);
        if(ApiSaveStatusEnum.API_SAVE.getCode().equals(param.getSaveType())){//保存
            apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.WAIT_PUBLISH.getCode());
        }else{
            apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
            apiBaseEntity.setApiVersion(1);
            apiBaseEntity.setPublishUser(userId);
            apiBaseEntity.setPublishTime(new Date());
        }
        saveOrUpdate(apiBaseEntity);

        String querySql = null;
        String requiredFieldStr = null;
        List<IcreditApiParamEntity> apiParamEntityList = new ArrayList<>();
        if(ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())) {//表单生成模式
            boolean isHaveRespField = false;
            StringBuffer querySqlPrefix = new StringBuffer("SELECT ");
            StringBuffer querySqlSuffix = new StringBuffer(SQL_WHERE);
            StringBuffer requiredFields = new StringBuffer();
            //保存 api param
            for (DatasourceApiParamSaveRequest datasourceApiParamSaveRequest : param.getApiParamSaveRequestList()) {
                IcreditApiParamEntity apiParamEntity = new IcreditApiParamEntity();
                apiParamEntity.setId(datasourceApiParamSaveRequest.getId());
                apiParamEntity.setApiBaseId(apiBaseEntity.getId());
                apiParamEntity.setApiVersion(apiBaseEntity.getApiVersion());
                apiParamEntity.setDesc(datasourceApiParamSaveRequest.getDesc());
                apiParamEntity.setRequired(datasourceApiParamSaveRequest.getRequired());
                apiParamEntity.setFieldName(datasourceApiParamSaveRequest.getFieldName());
                apiParamEntity.setFieldType(datasourceApiParamSaveRequest.getFieldType());
                apiParamEntity.setIsRequest(datasourceApiParamSaveRequest.getIsRequest());
                apiParamEntity.setIsResponse(datasourceApiParamSaveRequest.getIsResponse());
                apiParamEntityList.add(apiParamEntity);
                if(ResponseFiledEnum.IS_RESPONSE_FIELD.getCode().equals(datasourceApiParamSaveRequest.getIsResponse())){
                    querySqlPrefix.append(datasourceApiParamSaveRequest.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                    isHaveRespField = true;
                }
                if(RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(datasourceApiParamSaveRequest.getIsRequest())){
                    querySqlSuffix.append(datasourceApiParamSaveRequest.getFieldName())
                            .append(" = ${").append(datasourceApiParamSaveRequest.getFieldName()).append("}").append(SQL_AND);
                }
                if(RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(datasourceApiParamSaveRequest.getRequired())){
                    requiredFields.append(datasourceApiParamSaveRequest.getFieldName()).append(SQL_FIELD_SPLIT_CHAR);
                }
            }
            if(!isHaveRespField){//没有勾选返回参数
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
            }
            apiParamService.saveOrUpdateBatch(apiParamEntityList);
            querySql = String.valueOf(new StringBuffer(querySqlPrefix.substring(0, querySqlPrefix.lastIndexOf(SQL_FIELD_SPLIT_CHAR)))
                    .append(" FROM ").append(param.getApiGenerateSaveRequest().getTableName()).append(querySqlSuffix));
            if(querySql.endsWith(SQL_WHERE)){
                querySql = querySql.substring(0, querySql.lastIndexOf(SQL_WHERE));
            }
            if(querySql.endsWith(SQL_AND)){
                querySql = querySql.substring(0, querySql.lastIndexOf(SQL_AND));
            }
            if(requiredFields.length() >= 1) {
                requiredFieldStr = String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(","))));
            }
        }else{
            checkQuerySql(new CheckQuerySqlRequest(param.getApiGenerateSaveRequest().getDatasourceId(), param.getApiGenerateSaveRequest().getSql()));
        }

        //保存 generate api
        IcreditGenerateApiEntity generateApiEntity = new IcreditGenerateApiEntity();
        BeanUtils.copyProperties(param.getApiGenerateSaveRequest(), generateApiEntity);
        generateApiEntity.setId(param.getApiGenerateSaveRequest().getId());
        generateApiEntity.setApiBaseId(apiBaseEntity.getId());
        generateApiEntity.setApiVersion(apiBaseEntity.getApiVersion());
        if(ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())){
            generateApiEntity.setSql(querySql);
        }
        generateApiService.saveOrUpdate(generateApiEntity);

        //存放信息到redis
        BusinessResult<ConnectionInfoVO> connResult = dataSourceFeignClient.getConnectionInfo(new DataSourceInfoRequest(param.getApiGenerateSaveRequest().getDatasourceId()));
        ConnectionInfoVO connInfo = connResult.getData();
        RedisInterfaceInfo redisInterfaceInfo = new RedisInterfaceInfo();
        redisInterfaceInfo.setUrl(handleUrl(connInfo.getUrl()));
        redisInterfaceInfo.setUserName(connInfo.getUsername());
        redisInterfaceInfo.setPassword(connInfo.getPassword());
        redisInterfaceInfo.setQuerySql(ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel()) ? querySql : param.getApiGenerateSaveRequest().getSql());
        redisInterfaceInfo.setRequiredFields(requiredFieldStr);
        redisTemplate.opsForValue().set(String.valueOf(new StringBuilder(apiBaseEntity.getId()).append(apiBaseEntity.getApiVersion())), JSON.toJSONString(redisInterfaceInfo));
        ApiSaveResult apiSaveResult = new ApiSaveResult();
        apiSaveResult.setId(apiBaseEntity.getId());
        ApiGenerateSaveResult generateApiSaveResult = new ApiGenerateSaveResult();
        BeanUtils.copyProperties(generateApiEntity, generateApiSaveResult);
        apiSaveResult.setApiGenerateSaveRequest(generateApiSaveResult);
        List<ApiParamSaveResult> apiParamSaveResultList = BeanCopyUtils.copy(apiParamEntityList, ApiParamSaveResult.class);
        apiSaveResult.setApiParamSaveRequestList(apiParamSaveResultList);
        return BusinessResult.success(apiSaveResult);
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
        Connection conn = DBConnectionManager.getInstance().getConnection(uri, datasource.getType());
        List<FieldInfo> fieldList = new ArrayList<>();
        try {
            ResultSet rs = conn.getMetaData().getColumns(null, null, request.getTableName(), "%");
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
    public BusinessResult<Boolean> checkQuerySql(CheckQuerySqlRequest request) {
        if(StringUtils.isEmpty(request.getSql())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000008.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000008.getMessage());
        }
        String sql = request.getSql().replaceAll(" +", " ").toLowerCase();
        if(sql.contains("select *")){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000006.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000006.getMessage());
        }
        sql = "explain " + sql.replaceAll("\\$\\{.*?\\}", "''");
        DatasourceDetailResult datasource = getDatasourceDetail(request.getDatasourceId());
        String uri = datasource.getUri();
        Connection conn = DBConnectionManager.getInstance().getConnection(uri, datasource.getType());
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000007.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000007.getMessage());
//            e.printStackTrace();
        }finally {
            DBConnectionManager.getInstance().freeConnection(uri, conn);
        }
        return BusinessResult.success(true);
    }
}
