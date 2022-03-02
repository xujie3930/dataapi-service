package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisAppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.AuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.MapUtils;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    private static final String PATH_MARK = "path";
    private static final String VERSION_MARK = "apiVersion";
    private static final String SECRET_CONTENT_MARK = "secretContent";
    private static final Long NOT_LIMIT = -1L;
    private static final Long SECOND_OF_HOUR = 60 * 60 *1000L;

    @Override
    public BusinessResult<String> getToken(String appFlag, String secretContent) {
        Object value = redisTemplate.opsForValue().get(appFlag);
        if (Objects.isNull(value)){
            throw new AppException("应用状态异常!");
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(value.toString(), AppAuthInfo.class);
        if (secretContent.equals(appAuthInfo.getSecretContent())){
            throw new AppException("密钥验证失败!");
        }
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        appAuthInfo.setTokenCreateTime(System.currentTimeMillis());
        redisTemplate.opsForValue().set(token, JSON.toJSONString(appAuthInfo));
        return BusinessResult.success(token);
    }

    @Override
    public BusinessResult<List<Object>> getData() {
        ServletRequestAttributes requestAttributes = ServletRequestAttributes.class.
                cast(RequestContextHolder.getRequestAttributes());
        HttpServletRequest request = requestAttributes.getRequest();
        Map map = checkRequestParam(request);
        //1：根据token鉴权
        String token = (String) map.get(TOKEN_MARK);
        String path = (String) map.get(PATH_MARK);
        String version = (String) map.get(VERSION_MARK);
        AppAuthInfo appAuthInfo = checkApp(map, request);
        checkApi(request, map, appAuthInfo);
        return BusinessResult.success(null);
    }

    private void checkApi(HttpServletRequest request, Map map, AppAuthInfo appAuthInfo) {
        String path = (String) map.get(PATH_MARK);
        String version = (String) map.get(VERSION_MARK);
        Object apiObject = redisTemplate.opsForValue().get(new StringBuilder(path).append(REDIS_KEY_SPLIT_JOINT_CHAR).append(version));
        if (Objects.isNull(apiObject)){
            throw new AppException("API尚未发布!");
        }
        RedisApiInfo apiAuthInfo = JSON.parseObject(apiObject.toString(), RedisApiInfo.class);
        Object appAuthApiObject = redisTemplate.opsForValue().get(appAuthInfo.getGenerateId());
        if (Objects.isNull(appAuthApiObject)){
            throw new AppException("该应用未授权!");
        }
        Object appAuthAppObject = redisTemplate.opsForValue().get(apiAuthInfo.getApiId() + appAuthInfo.getGenerateId());
        if (Objects.isNull(appAuthAppObject)){
            throw new AppException("该API未授权该应用!");
        }
        RedisAppAuthInfo appAuthApp = JSON.parseObject(appAuthAppObject.toString(), RedisAppAuthInfo.class);
        if (null != appAuthApp.getPeriodEnd() && System.currentTimeMillis() > appAuthApp.getPeriodEnd()){
            throw new AppException("授权已到期");
        }
        //次数验证
        if (NOT_LIMIT != appAuthApp.getAllowCall().longValue() && appAuthApp.getAllowCall() <= 0){
            throw new AppException("已达调用次数上线");
        }
        //调用次数减一
        appAuthApp.setAllowCall(appAuthApp.getAllowCall() - 1);
        redisTemplate.opsForValue().set(apiAuthInfo.getApiId() + appAuthInfo.getGenerateId(),  JSON.toJSONString(appAuthApp));
    }

    private AppAuthInfo checkApp(Map map, HttpServletRequest request) {
        Object tokenObject = redisTemplate.opsForValue().get(map.get(TOKEN_MARK));
        if (Objects.isNull(tokenObject)){
            throw new AppException("token失效，请重新获取!");
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(tokenObject.toString(), AppAuthInfo.class);
        if (appAuthInfo.getIsEnable() == 0){
            throw new AppException("应用未启用！");
        }
        //IP白名单认证
        if (!StringUtils.isBlank(appAuthInfo.getAllowIp())){
            String remoteHost = request.getRemoteHost();
            Set<String> allowIpSet = new HashSet<>(Arrays.asList(appAuthInfo.getAllowIp().split(",")));
            if (!allowIpSet.contains(remoteHost)){
                throw new AppException("应用IP不在白名单内！");
            }
        }
        if (null != appAuthInfo.getPeriod()){
            Long expireTime = appAuthInfo.getPeriod() * SECOND_OF_HOUR + appAuthInfo.getTokenCreateTime();
            if (System.currentTimeMillis() >= expireTime){
                throw new AppException("token失效，请重新获取！");
            }
        }
        return appAuthInfo;
    }

    private Map checkRequestParam(HttpServletRequest request) {
        String queryString = request.getQueryString();
        Map map = MapUtils.str2Map(queryString);
        if (!map.containsKey(TOKEN_MARK)){
            String token = request.getHeader(TOKEN_MARK);
            if (StringUtils.isBlank(token)){
                throw new AppException("请求中缺失token！");
            }
            map.put(TOKEN_MARK, token);
        }
        if (!map.containsKey(PATH_MARK)){
            throw new AppException("请求中缺失API路径！");
        }
        if (!map.containsKey(VERSION_MARK)){
            throw new AppException("请求中缺失API版本！");
        }
        return map;
    }
}
