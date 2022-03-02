package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.AppAuthInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.RedisInterfaceInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.TokenInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.AuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.MapUtils;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
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
    private static final String TOKEN_MARK = "token";
    private static final String PATH_MARK = "path";
    private static final String VERSION_MARK = "apiVersion";
    private static final String SECRET_CONTENT_MARK = "secretContent";
    private static final Long NOT_LIMIT = -1L;

    @Override
    public BusinessResult<String> getToken(String appFlag) {
        Object value = redisTemplate.opsForValue().get(appFlag);
        if (Objects.isNull(value)){
            throw new AppException("应用状态异常");
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(value.toString(), AppAuthInfo.class);
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        appAuthInfo.setTokenUpdateTime(System.currentTimeMillis());
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
        checkToken(token, path, version);
        checkAppAuth(request, map, token);
        return BusinessResult.success(null);
    }

    private void checkAppAuth(HttpServletRequest request, Map map, String token) {
        //校验应用密钥
        Object redisObject1 = redisTemplate.opsForValue().get(token);
        if (Objects.isNull(redisObject1)){
            throw new AppException("token过期,请重新获取token！");
        }
        AppAuthInfo appAuthInfo = JSON.parseObject(redisObject1.toString(), AppAuthInfo.class);
        if (appAuthInfo.getIsEnable() == 0){
            //应用未启用报错
            throw new AppException("应用未启用！");
        }
        Integer certificationType = appAuthInfo.getCertificationType();
        //密钥认证
        if (certificationType == 0){
            String secretContent = (String) map.get(SECRET_CONTENT_MARK);
            if (!secretContent.equals(appAuthInfo.getSecretContent())){
                throw new AppException("密钥认证不通过,请确认密钥！");
            }
        }
        //TODO:证书认证未实现
        //IP白名单认证
        if (!StringUtils.isBlank(appAuthInfo.getAllowIp())){
            String remoteHost = request.getRemoteHost();
            System.out.println("ip : " + remoteHost);
            Set<String> allowIpSet = new HashSet<>(Arrays.asList(appAuthInfo.getAllowIp().split(",")));
            if (!allowIpSet.contains(remoteHost)){
                throw new AppException("白名单校验不通过");
            }
        }
        //次数验证
        if (NOT_LIMIT != appAuthInfo.getAllowCall().longValue() && appAuthInfo.getAllowCall() <= 0){
            throw new AppException("已达到次数上线");
        }
        appAuthInfo.setAllowCall(appAuthInfo.getAllowCall());
        redisTemplate.opsForValue().set(token,  JSON.toJSONString(appAuthInfo));
    }

    private void checkToken(String token, String path, String version) {
        Object redisObject = redisTemplate.opsForValue().get(path + version);
        if (Objects.isNull(redisObject)){
            throw new AppException("token校验失败！");
        }
        RedisInterfaceInfo redisInterfaceInfo = JSON.parseObject(redisObject.toString(), RedisInterfaceInfo.class);
        if (Objects.isNull(redisInterfaceInfo) || CollectionUtils.isEmpty(redisInterfaceInfo.getTokenList())){
            throw new AppException("token校验失败！");
        }
        Optional<TokenInfo> optional = redisInterfaceInfo.getTokenList().stream().filter(s -> s.getToken().equals(token)).findFirst();
        if (!optional.isPresent()){
            throw new AppException("token校验失败！");
        }
        TokenInfo tokenInfo = optional.get();
        if (!NOT_LIMIT.equals(tokenInfo.getPeriod())){
            Long period = tokenInfo.getPeriod();
            Long secondOfHours = period * 60 * 60 * 1000L;
            if (tokenInfo.getCreateTime() + secondOfHours <= System.currentTimeMillis()){
                throw new AppException("token过期,请重新去授权！");
            }
        }
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
            throw new AppException("请求中缺失API路径！");
        }
        return map;
    }
}
