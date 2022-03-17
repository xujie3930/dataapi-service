package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisAppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.MapUtils;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor
 * ClassName: ApiInterceptor
 * Description:  ApiInterceptor类
 * Date: 2022/3/17 10:36 上午
 *
 * @author liyanhui
 */
@Component
public class ApiInterceptor extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(ApiInterceptor.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    private static final Long NOT_LIMIT = -1L;
    private static final String REDIS_KEY_SPLIT_JOINT_CHAR = ":";
    private static final List<String> EXTRA_STR = Arrays.asList("v","V");
    private static final String TOKEN_MARK = "token";



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        int pathIndex = requestUri.lastIndexOf("/");
        String uriWithoutPath = requestUri.substring(0, pathIndex);
        String path = requestUri.substring(pathIndex + 1);
        int versionIndex = uriWithoutPath.lastIndexOf("/");
        String version = uriWithoutPath.substring(versionIndex + 1);
        logger.info("接收到请求：uri: {}, version: {}, path: {}", requestUri, version, path);
        //对请求版本号进行截取
        version = com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils.removeExtraStr(version, EXTRA_STR);
        ServletRequestAttributes requestAttributes = ServletRequestAttributes.class.
                cast(RequestContextHolder.getRequestAttributes());
        String token = checkRequestToken(request);
        AppAuthInfo appAuthInfo = getAppAuthInfoByToken(token);
        RedisApiInfo apiInfo = getApiAuthInfoByVersionAndPath(version, path);
        checkApi(apiInfo, appAuthInfo);
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
}
