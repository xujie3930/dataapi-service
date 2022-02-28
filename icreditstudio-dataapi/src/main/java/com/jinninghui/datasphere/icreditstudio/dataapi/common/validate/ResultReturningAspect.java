package com.jinninghui.datasphere.icreditstudio.dataapi.common.validate;

import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.InternalUserInfoVO;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.OauthApiService;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Aspect
@Component
@Slf4j
public class ResultReturningAspect {

    private static final Set<String> returnSet = new HashSet<>(Arrays.asList("createBy", "updateBy", "publishUser"));
    @Autowired
    private OauthApiService oauthApiService;

    /**
     * 定义切面，扫描所有service的实现类
     */
    @Pointcut("@annotation(com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning)")
    public void validatePointcut() {}

    @AfterReturning(value = "validatePointcut()", returning="returnValue")
    public void resultReturning(JoinPoint joinPoint, Object returnValue) throws IllegalAccessException {
        long startTime = System.currentTimeMillis();
        //减少feign调用，对取得的username作缓存
        Map<String, String> map = new HashMap<>();
        if (returnValue instanceof BusinessResult){
            returnValue = ((BusinessResult<?>) returnValue).getData();
        }
        if (returnValue instanceof Collection){
            for (Object obj : ((Collection<?>) returnValue)) {
                process(obj,map);
            }
        }else {
            process(returnValue, map);
        }
        Long spendTime = (System.currentTimeMillis() - startTime);
        log.info("后置处理耗时：" + spendTime + "毫秒");
    }

    private void process(Object obj, Map map) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (returnSet.contains(field.getName())){
                setNewValue(obj, map, field);
            }
        }
        Field[] superFields = obj.getClass().getSuperclass().getDeclaredFields();
        for (Field superField : superFields) {
            if (returnSet.contains(superField.getName())){
                setNewValue(obj, map, superField);
            }
        }
    }

    private void setNewValue(Object obj, Map<String, String> map, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        String fieldValue = (String) field.get(obj);
        if (map.containsKey(fieldValue)){
            field.set(obj, map.get(fieldValue));
            return;
        }
        if (StringUtils.isBlank(fieldValue)){
            return;
        }
        InternalUserInfoVO user = oauthApiService.getUserById(fieldValue);
        if (Objects.isNull(user)){
            return;
        }
        field.set(obj, user.getUsername());
        map.put(fieldValue, user.getUsername());
    }

}
