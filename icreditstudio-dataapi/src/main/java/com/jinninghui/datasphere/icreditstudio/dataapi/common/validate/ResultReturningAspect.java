package com.jinninghui.datasphere.icreditstudio.dataapi.common.validate;

import com.jinninghui.datasphere.icreditstudio.dataapi.service.OauthApiService;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
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
        if (returnValue instanceof BusinessResult){
            returnValue = ((BusinessResult<?>) returnValue).getData();
        }
        if (returnValue instanceof BusinessPageResult){
            returnValue = ((BusinessPageResult<?>) returnValue).getList();
        }
        Set<String> userIdSet = new HashSet<>();
        if (returnValue instanceof Collection){
            for (Object obj : ((Collection<?>) returnValue)) {
                getUserIds(obj, userIdSet);
            }
        }else {
            getUserIds(returnValue, userIdSet);
        }
        Map<String, String> map = oauthApiService.getUserNameBatch(new ArrayList<>(userIdSet));
        if (CollectionUtils.isEmpty(map)){
            return;
        }
        if (returnValue instanceof Collection){
            for (Object obj : ((Collection<?>) returnValue)) {
                process(obj, map);
            }
        }else {
            process(returnValue, map);
        }
        log.info("后置处理耗时：" + (System.currentTimeMillis() - startTime) + "毫秒");
    }

    private Set<String> getUserIds(Object obj, Set<String> set) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (returnSet.contains(field.getName())){
                //丢到统一的List中
                addToList(obj, field, set);
            }
        }
        Field[] superFields = obj.getClass().getSuperclass().getDeclaredFields();
        for (Field superField : superFields) {
            if (returnSet.contains(superField.getName())){
                addToList(obj, superField, set);
            }
        }
        return set;
    }

    private void addToList(Object obj, Field field, Set<String> set) throws IllegalAccessException {
        field.setAccessible(true);
        String fieldValue = (String) field.get(obj);
        set.add(fieldValue);
    }

    private void setNewValue(Object obj, Map<String, String> map, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        if (field.get(obj) instanceof Collection){
            Collection collection = (Collection) field.get(obj);
            for (Object junior : collection) {
                setNewValue(junior, map, field);
            }
        }
        String fieldValue = (String) field.get(obj);
        field.set(obj, map.get(fieldValue));
    }

    private void process(Object obj, Map map) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.get(obj) instanceof Collection){
                Collection collection = (Collection) field.get(obj);
                for (Object junior : collection) {
                    process(junior, map);
                }
            }
            if (returnSet.contains(field.getName())){
                setNewValue(obj, map, field);
            }
        }
        Field[] superFields = obj.getClass().getSuperclass().getDeclaredFields();
        for (Field superField : superFields) {
            superField.setAccessible(true);
            if (superField.get(obj) instanceof Collection){
                Collection collection = (Collection) superField.get(obj);
                for (Object junior : collection) {
                    process(junior, map);
                }
            }
            if (returnSet.contains(superField.getName())){
                setNewValue(obj, map, superField);
            }
        }
    }

}
