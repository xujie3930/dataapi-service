package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.AuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.MapUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.ResultSetToListUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.kafka.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.DBConnectionManager;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BusinessBasePageForm;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * <p>
 * 主题资源表 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2021-11-23
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_KEY_SPLIT_JOINT_CHAR = ":";
    private static final String TOKEN_MARK = "token";
    private static final String PAGENUM_MARK = "pageNum";
    private static final String PAGESIZE_MARK = "pageSize";
    private static final Long NOT_LIMIT = -1L;
    private static final Long SECOND_OF_HOUR = 60 * 60 * 1000L;
    private static final Integer PAGENUM_DEFALUT = 1;
    private static final Integer PAGESIZE_DEFALUT = 500;
    private static final List<String> EXTRA_STR = Arrays.asList("v","V");

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public BusinessResult<String> getToken(String generateId, String secretContent) {
        Object value = redisTemplate.opsForValue().get(generateId);
        if (Objects.isNull(value)){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000000.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000000.getMessage());
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(value.toString(), AppAuthInfo.class);
        if (!secretContent.equals(appAuthInfo.getSecretContent())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000001.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000001.getMessage());
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        appAuthInfo.setTokenCreateTime(System.currentTimeMillis());
        redisTemplate.opsForValue().set(token, JSON.toJSONString(appAuthInfo));
        return BusinessResult.success(token);
    }

    @Override
    public BusinessResult<Object> getData(String version, String path, Map map) {
        Connection conn = null;
        String querySql = null;
        Long dataCount = 0L;
        //对请求版本号进行截取
        version = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.removeExtraStr(version, EXTRA_STR);
        ServletRequestAttributes requestAttributes = ServletRequestAttributes.class.
                cast(RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = requestAttributes.getRequest();
        //检查请求中是否有token
        String token = checkRequestToken(request);
        AppAuthInfo appAuthInfo = getAppAuthInfoByToken(token);
        RedisApiInfo apiInfo = getApiAuthInfoByVersionAndPath(version, path);
        ApiLogInfo apiLogInfo = generateLog(appAuthInfo, apiInfo, version, path, request);
        try {
            //kafka推送消息
            kafkaProducer.send(apiLogInfo);
            //1：根据token,鉴权应用信息
            checkApp(appAuthInfo, request);
            //2：根据path和version,鉴权API信息
            checkApi(apiInfo, appAuthInfo);
            //3:对入参做校验
            checkParam(map, apiInfo);
            //处理sql，替换其中参数为入参
            querySql = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.parseSql(apiInfo.getQuerySql(), map);
            //连接数据源，执行SQL
            conn = DBConnectionManager.getInstance().getConnection(apiInfo.getUrl(), apiInfo.getUserName(), apiInfo.getPassword(), DatasourceTypeEnum.MYSQL.getType());
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //如果传了分页参数要加上分页 并且返回的数据要用分页对象包装:BusinessResult<BusinessPageResult> ，分页的最大条数500
            if (map.containsKey(PAGENUM_MARK) && map.containsKey(PAGESIZE_MARK)){
                BusinessPageResult<Object> build = getPageResult(map, querySql, dataCount, apiLogInfo, stmt);
                return BusinessResult.success(build);
            }else {
                //如果不传分页最大查询500条，不需要用分页对象包装
                List list = getListResult(querySql, dataCount, apiLogInfo, stmt);
                return BusinessResult.success(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //发送kafka失败信息
            ApiLogInfo failLog = generateFailLog(apiLogInfo, querySql, e);
            kafkaProducer.send(failLog);
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000013.getCode(), failLog.getExceptionDetail());
        } finally {
            DBConnectionManager.getInstance().freeConnection(apiInfo.getUrl(), conn);
        }
    }

    private List getListResult(String querySql, Long dataCount, ApiLogInfo apiLogInfo, Statement stmt) throws SQLException {
        querySql = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.addPageParam(querySql, PAGENUM_DEFALUT, PAGESIZE_DEFALUT);
        ResultSet pagingRs = stmt.executeQuery(querySql);
        if (pagingRs.next()) {
            List list = ResultSetToListUtils.convertList(pagingRs);
            //发送成功消息
            ApiLogInfo successLog = generateSuccessLog(apiLogInfo, querySql);
            kafkaProducer.send(successLog);
            return list;
        }
        return null;
    }

    private BusinessPageResult<Object> getPageResult(Map map, String querySql, Long dataCount, ApiLogInfo apiLogInfo, Statement stmt) throws SQLException {
        Integer pageNum = Math.max(Integer.parseInt((String) map.get(PAGENUM_MARK)), PAGENUM_DEFALUT);
        Integer pageSize = Math.min(Integer.parseInt((String) map.get(PAGESIZE_MARK)), PAGESIZE_DEFALUT);
        String countSql = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.getSelectCountSql(querySql);
        ResultSet countRs = stmt.executeQuery(countSql);
        if (countRs.next()) {
            //rs结果集第一个参数即为记录数，且其结果集中只有一个参数
            dataCount = countRs.getLong(1);
        }
        String pageSql = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.addPageParam(querySql, pageNum, pageSize);
        ResultSet pagingRsForPageParam = stmt.executeQuery(pageSql);
        if (pagingRsForPageParam.next()) {
            List list = ResultSetToListUtils.convertList(pagingRsForPageParam);
            //发送成功消息
            BusinessBasePageForm pageForm = new BusinessBasePageForm();
            pageForm.setPageNum(pageNum);
            pageForm.setPageSize(pageSize);
            BusinessPageResult build = BusinessPageResult.build(list, pageForm, dataCount);
            ApiLogInfo successLog = generateSuccessLog(apiLogInfo, pageSql);
            kafkaProducer.send(successLog);
            return build;
        }
        return null;
    }

    private ApiLogInfo generateFailLog(ApiLogInfo apiLogInfo, String querySql, Exception e) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_FAIL.getCode());
        if (null != apiLogInfo.getCallBeginTime()) {
            apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        }
        if (e instanceof AppException) {
            try {
                Field errorMsg = e.getClass().getSuperclass().getDeclaredField("errorMsg");
                ReflectionUtils.makeAccessible(errorMsg);
                String errorLog = (String) errorMsg.get(e);
                apiLogInfo.setExceptionDetail(errorLog);
            } catch (Exception exception) {
                exception.printStackTrace();
                apiLogInfo.setExceptionDetail(exception.toString());
            }
        } else {
            apiLogInfo.setExceptionDetail(e.toString());
        }
        return apiLogInfo;
    }

    private ApiLogInfo generateSuccessLog(ApiLogInfo apiLogInfo, String querySql) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_SUCCESS.getCode());
        apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        return apiLogInfo;
    }

    private ApiLogInfo generateLog(AppAuthInfo appAuthInfo, RedisApiInfo apiInfo, String version, String path, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        Date date = new Date();
        ApiLogInfo logInfo = new ApiLogInfo();
        logInfo.setTraceId(traceId);
        logInfo.setApiId(apiInfo.getApiId());
        logInfo.setApiPath(path);
        logInfo.setAppName(appAuthInfo.getName());
        logInfo.setAppId(appAuthInfo.getId());
        logInfo.setCallIp(request.getRemoteHost());
        logInfo.setApiVersion(Integer.valueOf(version));
        logInfo.setRequestParam(apiInfo.getRequiredFields());
        logInfo.setResponseParam(apiInfo.getResponseFields());
        logInfo.setCallStatus(CallStatusEnum.CALL_ON.getCode());
        logInfo.setCallBeginTime(date);
        logInfo.setCreateTime(date);
        logInfo.setUpdateTime(date);
        logInfo.setRunTime(0L);
        logInfo.setApiName(apiInfo.getApiName());
        logInfo.setApiType(apiInfo.getApiType());
        return logInfo;
    }

    private RedisApiInfo getApiAuthInfoByVersionAndPath(String version, String path) {
        Object apiObject = redisTemplate.opsForValue().get(String.valueOf(new StringBuilder(path).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(version)));
        if (Objects.isNull(apiObject)) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000002.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000002.getMessage());
        }
        RedisApiInfo apiAuthInfo = JSON.parseObject(apiObject.toString(), RedisApiInfo.class);
        apiAuthInfo.setQuerySql(apiAuthInfo.getQuerySql().toLowerCase());
        return apiAuthInfo;
    }

    //根据token获取应用信息
    private AppAuthInfo getAppAuthInfoByToken(String token) {
        Object tokenObject = redisTemplate.opsForValue().get(token);
        if (Objects.isNull(tokenObject)) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000003.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000003.getMessage());
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(tokenObject.toString(), AppAuthInfo.class);
        return appAuthInfo;
    }

    private Map checkParam(Map map, RedisApiInfo apiInfo) {
        map.remove(TOKEN_MARK);
        List<String> params = MapUtils.mapKeyToList(map);
        if (StringUtils.isNotBlank(apiInfo.getRequiredFields())) {
            Set<String> requestList = new HashSet<>(Arrays.asList(apiInfo.getRequiredFields().split(",")));
            if (!CollectionUtils.isEmpty(requestList) && !params.containsAll(requestList)) {
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000004.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000004.getMessage());
            }
        }
        return map;
    }

    private RedisApiInfo checkApi(RedisApiInfo apiAuthInfo, AppAuthInfo appAuthInfo) {
        Object appAuthApiObject = redisTemplate.opsForValue().get(appAuthInfo.getGenerateId());
        if (Objects.isNull(appAuthApiObject)) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000005.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000005.getMessage());
        }
        Object appAuthAppObject = redisTemplate.opsForValue().get(apiAuthInfo.getApiId() + appAuthInfo.getGenerateId());
        if (Objects.isNull(appAuthAppObject)) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000006.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000006.getMessage());
        }
        RedisAppAuthInfo appAuthApp = JSON.parseObject(appAuthAppObject.toString(), RedisAppAuthInfo.class);
        if (!NOT_LIMIT.equals(appAuthApp.getPeriodEnd()) && System.currentTimeMillis() > appAuthApp.getPeriodEnd()) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000007.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000007.getMessage());
        }
        //次数验证
        if (!NOT_LIMIT.equals(appAuthApp.getAllowCall().longValue()) && appAuthApp.getAllowCall() <= 0) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000008.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000008.getMessage());
        }
        //调用次数减一
        appAuthApp.setAllowCall(appAuthApp.getAllowCall() - 1);
        redisTemplate.opsForValue().set(apiAuthInfo.getApiId() + appAuthInfo.getGenerateId(), JSON.toJSONString(appAuthApp));
        return apiAuthInfo;
    }

    private AppAuthInfo checkApp(AppAuthInfo appAuthInfo, HttpServletRequest request) {
        if (AppEnableEnum.NOT_ENABLE.getCode().equals(appAuthInfo.getIsEnable())) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000009.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000009.getMessage());
        }
        //IP白名单认证
        if (!StringUtils.isBlank(appAuthInfo.getAllowIp())) {
            String remoteHost = request.getRemoteHost();
            Set<String> allowIpSet = new HashSet<>(Arrays.asList(appAuthInfo.getAllowIp().split(",")));
            if (!allowIpSet.contains(remoteHost)) {
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000010.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000010.getMessage());
            }
        }
        if (null != appAuthInfo.getPeriod()) {
            Long expireTime = appAuthInfo.getPeriod() * SECOND_OF_HOUR + appAuthInfo.getTokenCreateTime();
            if (System.currentTimeMillis() >= expireTime) {
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000011.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000011.getMessage());
            }
        }
        return appAuthInfo;
    }

    private String checkRequestToken(HttpServletRequest request) {
        String queryString = request.getQueryString();
        Map map = MapUtils.str2Map(queryString);
        if (!map.containsKey(TOKEN_MARK)){
            String token = request.getHeader(TOKEN_MARK);
            if (StringUtils.isBlank(token)){
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000012.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000012.getMessage());
            }
            return token;
        }
        return (String) map.get(TOKEN_MARK);
    }
}
