package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.MapUtils;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor
 * ClassName: DataApiGatewayInterceptor
 * Description:  DataApiGatewayInterceptor类
 * Date: 2022/3/17 10:36 上午
 *
 * @author liyanhui
 */
@Component
public class DataApiGatewayInterceptor extends HandlerInterceptorAdapter {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    protected RedisTemplate<String, Object> redisTemplate;
    @Resource
    private KafkaProducer kafkaProducer;
    protected static final String REDIS_KEY_SPLIT_JOINT_CHAR = ":";
    protected static final String TOKEN_MARK = "token";
    protected static final String PAGENUM_MARK = "pageNum";
    protected static final String PAGESIZE_MARK = "pageSize";
    protected static final Long NOT_LIMIT = -1L;
    protected static final Long SECOND_OF_HOUR = 60 * 60 * 1000L;
    protected static final Integer PAGENUM_DEFALUT = 1;
    protected static final Integer PAGESIZE_DEFALUT = 500;
    protected static final List<String> EXTRA_STR = Arrays.asList("v", "V");
    protected static final String ALL_NETWORK = "0.0.0.0";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestUri = request.getRequestURI();
        int pathIndex = requestUri.lastIndexOf("/");
        String uriWithoutPath = requestUri.substring(0, pathIndex);
        String path = requestUri.substring(pathIndex + 1);

        int versionIndex = uriWithoutPath.lastIndexOf("/");
        String version = uriWithoutPath.substring(versionIndex + 1);
        //对请求版本号进行截取
        version = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.removeExtraStr(version, EXTRA_STR);

        logger.info("接收到请求：uri: {}, version: {}, path: {}", requestUri, version, path);

        RedisApiInfo apiInfo = getApiAuthInfoByVersionAndPath(version, path);

        String queryString = request.getQueryString();
        Map paramMap = MapUtils.str2Map(queryString);

        String token = checkRequestToken(request);

        AppAuthInfo appAuthInfo = getAppAuthInfoByToken(token);
        ApiLogInfo apiLogInfo = generateLog(appAuthInfo, apiInfo, version, path, request, paramMap);

        try {
            //kafka推送消息
            kafkaProducer.send(apiLogInfo);
            //1：根据token,鉴权应用信息
            checkApp(appAuthInfo, request, token);
            //2：根据path和version,鉴权API信息
            checkApi(apiInfo, appAuthInfo);
            //3:对入参做校验
            checkParam(paramMap, apiInfo);

        } catch (Exception e) {
            e.printStackTrace();
            //发送kafka失败信息
            ApiLogInfo failLog = generateFailLog(apiLogInfo, e);
            kafkaProducer.send(failLog);
            logger.info("发送kafka异常日志:{}", failLog);
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000013.getCode(), failLog.getExceptionDetail());
        }
        DataApiGatewayContext context = new DataApiGatewayContext();
        context.setApiInfo(apiInfo);
        context.setApiLogInfo(apiLogInfo);
        DataApiGatewayContextHolder.set(context);
        return super.preHandle(request, response, handler);
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
        //授权时间验证
        if (null != appAuthApp.getPeriodBegin() && !NOT_LIMIT.equals(appAuthApp.getPeriodBegin()) &&
                System.currentTimeMillis() < appAuthApp.getPeriodBegin()) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000014.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000014.getMessage());
        }
        if (null != appAuthApp.getPeriodEnd() && !NOT_LIMIT.equals(appAuthApp.getPeriodEnd()) &&
                System.currentTimeMillis() > appAuthApp.getPeriodEnd()) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000015.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000015.getMessage());
        }
        //次数验证
        if (!NOT_LIMIT.equals(appAuthApp.getAllowCall().longValue()) && appAuthApp.getCalled() >= appAuthApp.getAllowCall()) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000008.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000008.getMessage());
        }
        //调用次数加一
        appAuthApp.setCalled(appAuthApp.getCalled() + 1);
        redisTemplate.opsForValue().set(apiAuthInfo.getApiId() + appAuthInfo.getGenerateId(), JSON.toJSONString(appAuthApp));
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

    private RedisApiInfo getApiAuthInfoByVersionAndPath(String version, String path) {
        Object apiObject = redisTemplate.opsForValue().get(String.valueOf(new StringBuilder(path).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(version)));
        if (Objects.isNull(apiObject)) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000002.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000002.getMessage());
        }
        RedisApiInfo apiAuthInfo = JSON.parseObject(apiObject.toString(), RedisApiInfo.class);
        return apiAuthInfo;
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

    private AppAuthInfo checkApp(AppAuthInfo appAuthInfo, HttpServletRequest request, String token) {
        if (AppEnableEnum.NOT_ENABLE.getCode().equals(appAuthInfo.getIsEnable())) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000009.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000009.getMessage());
        }
        //IP白名单认证
        if (!StringUtils.isBlank(appAuthInfo.getAllowIp())) {
            String remoteHost = request.getRemoteHost();
            Set<String> allowIpSet = new HashSet<>(Arrays.asList(appAuthInfo.getAllowIp().split(",")));
            if (!allowIpSet.contains(ALL_NETWORK) && !allowIpSet.contains(remoteHost)) {
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000010.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000010.getMessage());
            }
        }
        if (null != appAuthInfo.getPeriod() && !NOT_LIMIT.equals(appAuthInfo.getPeriod().longValue())) {
            Long expireTime = appAuthInfo.getPeriod() * SECOND_OF_HOUR + appAuthInfo.getTokenCreateTime();
            if (System.currentTimeMillis() >= expireTime) {
                redisTemplate.delete(token);
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000011.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000011.getMessage());
            }
        }
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

    private ApiLogInfo generateFailLog(ApiLogInfo apiLogInfo, Exception e) {
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

    private ApiLogInfo generateLog(AppAuthInfo appAuthInfo, RedisApiInfo apiInfo, String version, String path, HttpServletRequest request, Map map) {
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        Date date = new Date();
        ApiLogInfo logInfo = new ApiLogInfo();
        logInfo.setTraceId(traceId);
        logInfo.setApiId(apiInfo.getApiId());
        logInfo.setApiPath(path);
        logInfo.setAppName(appAuthInfo.getName());
        logInfo.setAppId(appAuthInfo.getId());
        logInfo.setCallIp(request.getRemoteAddr());
        logInfo.setApiVersion(Integer.valueOf(version));
        map.remove(TOKEN_MARK);
        List<String> params = MapUtils.mapKeyToList(map);
        if (!CollectionUtils.isEmpty(params)){
            logInfo.setRequestParam(String.join(",", params));
        }
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
}
