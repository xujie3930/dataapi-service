package com.jinninghui.datasphere.icreditstudio.framework.facade;

import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author liyanhui
 */
@Component
@Aspect
public class DaoInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(DaoInterceptor.class);
    private static final int PARAM_LENGTH_MAX = 100;

    @Pointcut("execution(* com.jinninghui..mapper..*(..))")
    public void pointcut(){

    }

    @Around("pointcut()")
    public Object invoke(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod();
        String methodName = method.getDeclaringClass().getName() + ":" + method.getName();
        Object[] objects = proceedingJoinPoint.getArgs();
        String paramString = Arrays.toString(objects);
        if(StringUtils.hasText(paramString) && paramString.length() > PARAM_LENGTH_MAX){
            paramString = paramString.substring(0, PARAM_LENGTH_MAX);
        }
        logger.info("调用数据服务:" + methodName + ". param=" + paramString + ";");
        Object returnVal;
        try {
            returnVal = proceedingJoinPoint.proceed();
        } catch (Exception e) {
            logger.error("服务{}调用异常,", methodName, e);
            throw e;
        }
        return returnVal;
    }
}
