package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppEnableEnum;
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
import java.util.*;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor
 * ClassName: AppInterceptor
 * Description:  AppInterceptor类
 * Date: 2022/3/17 10:36 上午
 *
 * @author liyanhui
 */
@Component
public class AppInterceptor extends HandlerInterceptorAdapter {
    private Logger logger = LoggerFactory.getLogger(AppInterceptor.class);

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
    private static final String ALL_NETWORK = "0.0.0.0";

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
        checkApp(appAuthInfo, request, token);
        return super.preHandle(request, response, handler);
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

    //根据token获取应用信息
    private AppAuthInfo getAppAuthInfoByToken(String token) {
        Object tokenObject = redisTemplate.opsForValue().get(token);
        if (Objects.isNull(tokenObject)) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000003.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000003.getMessage());
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(tokenObject.toString(), AppAuthInfo.class);
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
