package com.jinninghui.datasphere.icreditstudio.dataapi.common.validate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.InternalUserInfoVO;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.OauthApiService;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
public class ResultReturningAspect {

    @Autowired
    private OauthApiService oauthApiService;

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 定义切面，扫描所有service的实现类
     */
    @Pointcut("@annotation(com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning)")
    public void validatePointcut() {}

    @AfterReturning(value = "validatePointcut()", returning="returnValue")
    public void resultReturning(JoinPoint jp, Object returnValue) throws Throwable {
        if (returnValue instanceof BusinessResult){
            returnValue = ((BusinessResult<?>) returnValue).getData();
        }
        if (returnValue instanceof Collection){
            for (Object obj : ((Collection<?>) returnValue)) {
                Field updateBy = obj.getClass().getSuperclass().getDeclaredField("updateBy");
                updateBy.setAccessible(true);
                String updateByFieldValue = (String) updateBy.get(obj);
                InternalUserInfoVO updateUser = oauthApiService.getUserById(updateByFieldValue);
                updateBy.set(obj, updateUser.getUsername());
                Field createBy = obj.getClass().getSuperclass().getDeclaredField("createBy");
                createBy.setAccessible(true);
                String createByFieldValue = (String) createBy.get(obj);
                InternalUserInfoVO createUser = oauthApiService.getUserById(createByFieldValue);
                updateBy.set(obj, createUser.getUsername());
            }
        }


        /*String createBy = jsonObject.getString("createBy");
        String updateBy = jsonObject.getString("updateBy");
        InternalUserInfoVO createUser = oauthApiService.getUserById(createBy);
        InternalUserInfoVO updateUser = oauthApiService.getUserById(updateBy);
        Field[] declaredFields = returnValue.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equals("createBy")){
                field.setAccessible(true);
                field.set(returnValue, createUser.getUsername());
            }
            if (field.getName().equals("updateBy")){
                field.setAccessible(true);
                field.set(returnValue, updateUser.getUsername());
            }
        }*/
    }

}
