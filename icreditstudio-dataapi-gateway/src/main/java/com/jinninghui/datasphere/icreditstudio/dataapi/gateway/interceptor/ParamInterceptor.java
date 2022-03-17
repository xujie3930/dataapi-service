package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.MapUtils;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
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
 * ClassName: ParamInterceptor
 * Description:  ParamInterceptor类
 * Date: 2022/3/17 10:36 上午
 *
 * @author liyanhui
 */
@Component
public class ParamInterceptor extends HandlerInterceptorAdapter implements DataApiGatewayInterceptor {
    private Logger logger = LoggerFactory.getLogger(ParamInterceptor.class);

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
        RedisApiInfo apiInfo = getApiAuthInfoByVersionAndPath(version, path);
        String queryString = request.getQueryString();
        Map paramMap = MapUtils.str2Map(queryString);
        checkParam(paramMap, apiInfo);
        return super.preHandle(request, response, handler);
    }

    private RedisApiInfo getApiAuthInfoByVersionAndPath(String version, String path) {
        Object apiObject = redisTemplate.opsForValue().get(String.valueOf(new StringBuilder(path).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(version)));
        if (Objects.isNull(apiObject)) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000002.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000002.getMessage());
        }
        RedisApiInfo apiAuthInfo = JSON.parseObject(apiObject.toString(), RedisApiInfo.class);
        return apiAuthInfo;
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
}
