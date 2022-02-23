package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisInterfaceInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiParamEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditGenerateApiEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiModelTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiPublishStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.RequestFiledEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ResponseFiledEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.DatasourceFeignClient;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DataSourceInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.ConnectionInfoVO;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiParamService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditGenerateApiService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiBaseListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.DatasourceApiParamSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiBaseResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.Query;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
            apiBaseResult.setPublishTime(record.getPublishTime().getTime());
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
    public BusinessResult<Boolean> createDataSourceApi(DatasourceApiSaveParam param) {
        //校验api path和api name
        checkPathAndName(param.getPath(), param.getName());
        if((ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel()) && CollectionUtils.isEmpty(param.getApiParamSaveRequestList())) ||
                null == param.getApiGenerateSaveRequest()){
            //入参错误
        }

        //保存api基础信息
        IcreditApiBaseEntity apiBaseEntity = new IcreditApiBaseEntity();
        BeanUtils.copyProperties(param, apiBaseEntity);
        apiBaseEntity.setPublishStatus(ApiPublishStatusEnum.PUBLISHED.getCode());
        save(apiBaseEntity);

        StringBuffer querySqlPrefix = new StringBuffer("SELECT ");
        StringBuffer querySqlSuffix = new StringBuffer(" WHERE ");
        StringBuffer requiredFields = new StringBuffer();
        boolean isHaveRespField = false;
        List<IcreditApiParamEntity> apiParamEntityList = new ArrayList<>();
        if(ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())) {//表单生成模式
            //保存 api param
            for (DatasourceApiParamSaveRequest datasourceApiParamSaveRequest : param.getApiParamSaveRequestList()) {
                IcreditApiParamEntity apiParamEntity = new IcreditApiParamEntity();
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
                    querySqlPrefix.append(datasourceApiParamSaveRequest.getFieldName()).append(",");
                    isHaveRespField = true;
                }
                if(RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(datasourceApiParamSaveRequest.getIsRequest())){
                    querySqlSuffix.append(datasourceApiParamSaveRequest.getFieldName())
                            .append(" = ${").append(datasourceApiParamSaveRequest.getFieldName()).append("}").append(" AND ");
                }
                if(RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(datasourceApiParamSaveRequest.getRequired())){
                    requiredFields.append(datasourceApiParamSaveRequest.getFieldName()).append(",");
                }
            }
            if(!isHaveRespField){//没有勾选返回参数
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000004.getMessage());
            }
            apiParamService.saveBatch(apiParamEntityList);
        }
        String querySql = String.valueOf(new StringBuffer(querySqlPrefix.substring(0, querySqlPrefix.lastIndexOf(",")))
                .append(" FROM ").append(param.getApiGenerateSaveRequest().getTableName()).append(querySqlSuffix));
        if(querySql.endsWith(" WHERE ")){
            querySql = querySql.substring(0, querySql.lastIndexOf(" WHERE "));
        }
        if(querySql.endsWith(" AND ")){
            querySql = querySql.substring(0, querySql.lastIndexOf(" AND "));
        }

        //保存 generate api
        IcreditGenerateApiEntity generateApiEntity = new IcreditGenerateApiEntity();
        BeanUtils.copyProperties(param.getApiGenerateSaveRequest(), generateApiEntity);
        generateApiEntity.setApiBaseId(apiBaseEntity.getId());
        generateApiEntity.setApiVersion(apiBaseEntity.getApiVersion());
        if(ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel())){
            generateApiEntity.setSql(querySql);
        }
        generateApiService.save(generateApiEntity);

        //存放信息到redis
        BusinessResult<ConnectionInfoVO> connResult = dataSourceFeignClient.getConnectionInfo(new DataSourceInfoRequest(param.getApiGenerateSaveRequest().getDatasourceId()));
        ConnectionInfoVO connInfo = connResult.getData();
        RedisInterfaceInfo redisInterfaceInfo = new RedisInterfaceInfo();
        redisInterfaceInfo.setUrl(handleUrl(connInfo.getUrl()));
        redisInterfaceInfo.setUserName(connInfo.getUsername());
        redisInterfaceInfo.setPassword(connInfo.getPassword());
        redisInterfaceInfo.setQuerySql(ApiModelTypeEnum.SINGLE_TABLE_CREATE_MODEL.getCode().equals(param.getApiGenerateSaveRequest().getModel()) ? querySql : param.getApiGenerateSaveRequest().getSql());
        redisInterfaceInfo.setRequiredFields(String.valueOf(new StringBuffer(requiredFields.substring(0, requiredFields.lastIndexOf(",")))));
        redisTemplate.opsForValue().set(String.valueOf(new StringBuilder(apiBaseEntity.getId()).append(apiBaseEntity.getApiVersion())), JSON.toJSONString(redisInterfaceInfo));
        return BusinessResult.success(true);
    }

    private void checkPathAndName(String path, String name) {
        if (!path.matches("[a-zA-Z]{16}")) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000001.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000001.getMessage());
        }
        if (!name.matches("[a-zA-Z0-9\u4e00-\u9fa5_]{1,50}")) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000002.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000002.getMessage());
        }
        Boolean isExist = apiBaseMapper.isExistByName(name);
        if(null != isExist && isExist){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000003.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000003.getMessage());
        }
        isExist = apiBaseMapper.isExistByPath(path);
        if(null != isExist && isExist){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000005.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000005.getMessage());
        }
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
}
