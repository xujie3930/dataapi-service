package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.AuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.MapUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.ResultSetToListUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.kafka.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.Date;
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
    private static final Long NOT_LIMIT = -1L;
    private static final Long SECOND_OF_HOUR = 60 * 60 *1000L;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public BusinessResult<String> getToken(String appFlag, String secretContent) {
        Object value = redisTemplate.opsForValue().get(appFlag);
        if (Objects.isNull(value)){
            throw new AppException("应用状态异常!");
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(value.toString(), AppAuthInfo.class);
        if (!secretContent.equals(appAuthInfo.getSecretContent())){
            throw new AppException("密钥验证失败!");
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        appAuthInfo.setTokenCreateTime(System.currentTimeMillis());
        redisTemplate.opsForValue().set(token, JSON.toJSONString(appAuthInfo));
        return BusinessResult.success(token);
    }

    @Override
    public BusinessResult<List<Object>> getData(String version, String path,  Map map) {
        Connection conn = null;
        String querySql = null;
        ApiLogInfo apiLogInfo = new ApiLogInfo();
        try {
            ServletRequestAttributes requestAttributes = ServletRequestAttributes.class.
                    cast(RequestContextHolder.getRequestAttributes());
            HttpServletRequest request = requestAttributes.getRequest();
            //检查请求中是否有token
            String token = checkRequestToken(request);
            AppAuthInfo appAuthInfo = getAppAuthInfoByToken(token);
            RedisApiInfo apiInfo = getApiAuthInfoByVersionAndPath(version, path);
            apiLogInfo = generateLog(appAuthInfo, apiInfo, version, path, request);
            //记录日志唯一id
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
            conn = DriverManager.getConnection(apiInfo.getUrl(), apiInfo.getUserName(), apiInfo.getPassword());
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet pagingRs = stmt.executeQuery(querySql);
            if (pagingRs.next()) {
                List list = ResultSetToListUtils.convertList(pagingRs);
                //发送成功消息
                ApiLogInfo successLog = generateSuccessLog(apiLogInfo, querySql);
                kafkaProducer.send(successLog);
                return BusinessResult.success(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //发送kafka失败信息
            ApiLogInfo failLog = generateFailLog(apiLogInfo, querySql, e);
            kafkaProducer.send(failLog);
        } finally {
            if (null != conn) {
                try {
                    conn.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return BusinessResult.success(null);
    }

    private ApiLogInfo generateFailLog(ApiLogInfo apiLogInfo, String querySql, Exception e) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_FAIL.getCode());
        apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getRunTime());
        apiLogInfo.setErrorLog(e.toString());
        return apiLogInfo;
    }

    private ApiLogInfo generateSuccessLog(ApiLogInfo apiLogInfo, String querySql) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_SUCCESS.getCode());
        apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getRunTime());
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
        logInfo.setApiVersion(version);
        //请求参数
        logInfo.setRequestParam("");
        //返回参数
        logInfo.setResponsePatam("");
        //请求开始时间
        logInfo.setCallStatus(CallStatusEnum.CALL_ON.getCode());
        logInfo.setCallBeginTime(date);
        logInfo.setCreateTime(date);
        logInfo.setUpdateTime(date);
        logInfo.setRunTime(System.currentTimeMillis());
        return logInfo;
    }

    private RedisApiInfo getApiAuthInfoByVersionAndPath(String version, String path) {
        Object apiObject = redisTemplate.opsForValue().get(String.valueOf(new StringBuilder(path).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(version)));
        if (Objects.isNull(apiObject)) {
            throw new AppException("API尚未发布!");
        }
        RedisApiInfo apiAuthInfo = JSON.parseObject(apiObject.toString(), RedisApiInfo.class);
        return apiAuthInfo;
    }

    //根据token获取应用信息
    private AppAuthInfo getAppAuthInfoByToken(String token) {
        Object tokenObject = redisTemplate.opsForValue().get(token);
        if (Objects.isNull(tokenObject)) {
            throw new AppException("token失效，请重新获取!");
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(tokenObject.toString(), AppAuthInfo.class);
        return appAuthInfo;
    }

    private Map checkParam(Map map, RedisApiInfo apiInfo) {
        map.remove(TOKEN_MARK);
        List<String> params = MapUtils.mapKeyToList(map);
        if (StringUtils.isNotBlank(apiInfo.getRequiredFields())) {
            Set<String> requestList = new HashSet<>(Arrays.asList(apiInfo.getRequiredFields().split(",")));
            if (!CollectionUtils.isEmpty(requestList) && !requestList.containsAll(params)) {
                throw new AppException("参数缺失!");
            }
        }
        return map;
    }

    private RedisApiInfo checkApi(RedisApiInfo apiAuthInfo, AppAuthInfo appAuthInfo) {
        Object appAuthApiObject = redisTemplate.opsForValue().get(appAuthInfo.getGenerateId());
        if (Objects.isNull(appAuthApiObject)) {
            throw new AppException("该应用未授权!");
        }
        Object appAuthAppObject = redisTemplate.opsForValue().get(apiAuthInfo.getApiId() + appAuthInfo.getGenerateId());
        if (Objects.isNull(appAuthAppObject)) {
            throw new AppException("该API未授权该应用!");
        }
        RedisAppAuthInfo appAuthApp = JSON.parseObject(appAuthAppObject.toString(), RedisAppAuthInfo.class);
        if (null != appAuthApp.getPeriodEnd() && System.currentTimeMillis() > appAuthApp.getPeriodEnd()) {
            throw new AppException("授权已到期");
        }
        //次数验证
        if (NOT_LIMIT != appAuthApp.getAllowCall().longValue() && appAuthApp.getAllowCall() <= 0) {
            throw new AppException("已达调用次数上线");
        }
        //调用次数减一
        appAuthApp.setAllowCall(appAuthApp.getAllowCall() - 1);
        redisTemplate.opsForValue().set(apiAuthInfo.getApiId() + appAuthInfo.getGenerateId(), JSON.toJSONString(appAuthApp));
        return apiAuthInfo;
    }

    private AppAuthInfo checkApp(AppAuthInfo appAuthInfo, HttpServletRequest request) {
        if (appAuthInfo.getIsEnable() == 0) {
            throw new AppException("应用未启用！");
        }
        //IP白名单认证
        if (!StringUtils.isBlank(appAuthInfo.getAllowIp())) {
            String remoteHost = request.getRemoteHost();
            Set<String> allowIpSet = new HashSet<>(Arrays.asList(appAuthInfo.getAllowIp().split(",")));
            if (!allowIpSet.contains(remoteHost)) {
                throw new AppException("应用IP不在白名单内！");
            }
        }
        if (null != appAuthInfo.getPeriod()) {
            Long expireTime = appAuthInfo.getPeriod() * SECOND_OF_HOUR + appAuthInfo.getTokenCreateTime();
            if (System.currentTimeMillis() >= expireTime) {
                throw new AppException("token失效，请重新获取！");
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
                throw new AppException("请求中缺失token！");
            }
            return token;
        }
        return (String) map.get(TOKEN_MARK);
    }
}
