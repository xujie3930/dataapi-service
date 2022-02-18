package com.jinninghui.datasphere.icreditstudio.framework.log;

import com.alibaba.fastjson.JSONObject;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.feign.FeignExceptionDTO;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.feign.FeignExceptionUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.Constants;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import feign.FeignException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Logable注解的切面实现
 * @author Lida
 */
@Aspect
@Component
public class LogRecorder {

    private final static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final static AtomicLong COUNTER = new AtomicLong(1000000);
    private final static String LOG_POSITION_POST = "POST";
    private final static String LOG_POSITION_PRE = "PRE";

    private final static String SPLIT = "|";

    private final static String EXCEPTION_SPLIT = "******";

    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${encryKey:''}")
    private String encryKey;

    @Autowired
    private ServerEnv serverEnv;

    public static final String LOG_LEVEL_DEBUG = "DEBUG";

    public static final String LOG_LEVEL_INFO = "INFO";

    @Around("@annotation(com.jinninghui.datasphere.icreditstudio.framework.log.Logable)")
    public Object log(ProceedingJoinPoint pjp) throws Throwable {
        final long start = System.currentTimeMillis();
        final long end;
        //final long count = COUNTER.incrementAndGet();
        Throwable t = null;
        Object result = null;
        Class<?> classTarget = pjp.getTarget().getClass();
        Method currentMethod = LogUtil.getCurrentMethod(pjp);
        Object[] args = pjp.getArgs();
        Logable log = currentMethod.getAnnotation(Logable.class);
        Logger detailLogger = LogUtil.getLogger(log.loggerName() + "-detail");
        String beginLogTexs = this.buildSimpleLogText(log, classTarget, currentMethod, t, null, LOG_POSITION_PRE) + buildBeginLogText(log, args);
        if (detailLogger.isInfoEnabled() && log.level().name().equals(LOG_LEVEL_INFO)) {
            detailLogger.info(beginLogTexs);
        } else if (detailLogger.isDebugEnabled() && log.level().name().equals(LOG_LEVEL_DEBUG)) {
            detailLogger.debug(beginLogTexs);
        }
        try {
            result = pjp.proceed();
        } catch (Throwable t1) {
            t = t1;
        } finally {
            end = System.currentTimeMillis();
        }
        String endLogTexs = this.buildSimpleLogText(log, classTarget, currentMethod, t, (end - start), LOG_POSITION_POST) + this.buildEndLogText(log, result, t);
        if (t == null) {
            if (detailLogger.isInfoEnabled() && log.level().name().equals(LOG_LEVEL_INFO)) {
                detailLogger.info(endLogTexs);
            } else if (detailLogger.isDebugEnabled() && log.level().name().equals(LOG_LEVEL_DEBUG)) {
                detailLogger.debug(endLogTexs);
            }
        } else {
            detailLogger.error(endLogTexs);
        }

        if (t == null) {
            return result;
        } else {
            throw t;
        }
    }

    
    /**
     * @param log
     * @param clazz
     * @param method
     * @param exception
     * @param time
     * @return
     */
    protected final String buildSimpleLogText(Logable log, Class<?> clazz, Method method, Throwable exception, Long time, String position) {
        final StringBuilder sb = new StringBuilder(DF.format(ZonedDateTime.now()));
        sb.append(SPLIT).append(this.serverEnv.getServerIp()).append(":").append(this.serverEnv.getServerPort());
        sb.append(SPLIT).append(applicationName);
        sb.append(SPLIT).append(clazz.getName());
        sb.append(SPLIT).append(method.getName());
        sb.append(SPLIT).append(LOG_POSITION_POST.equals(position)? (time+"ms"): (0L+"ms"));
        sb.append(SPLIT).append(LOG_POSITION_POST.equals(position)? ("errorCode:"+getReturnCode(exception)): "-");
        sb.append(SPLIT).append(LOG_POSITION_POST.equals(position)? (exception == null ? "SUCCESS" : "FAIL"): "-");
        sb.append(SPLIT).append(position);
        sb.append(SPLIT).append(LogUtil.getTransaction(log));
        return sb.toString();
    }

    /**
     * 开始日志
     *
     * @param log
     * @param args
     * @return
     */
    protected final String buildBeginLogText(Logable log, Object[] args) {
        final StringBuilder sb = new StringBuilder();
        List<Object> objects = LogUtil.args(args);
        int[] encryptArgsIndex = log.encryptArgsIndex();
        int[] ignoreOutputArgsIndex = log.ignoreOutputArgsIndex();
        sb.append(SPLIT + "Args: [");
        if(objects != null && !objects.isEmpty()){
            for(Object argSource : objects) {
                boolean encrypt = false;
                boolean output = true;
                for (int index : encryptArgsIndex) {
                    Object arg = objects.get(index);
                    if(argSource.equals(arg)){
                        encrypt = true;
                        break;
                    }
                }
                for (int index : ignoreOutputArgsIndex) {
                    Object arg = objects.get(index);
                    if(argSource.equals(arg)){
                        output = false;
                        break;
                    }
                }
                String jsonObjects = JSONObject.toJSONString(argSource);
                if(encrypt && log.encrypt() && StringUtils.hasText(encryKey)){
                    jsonObjects = LogUtil.encrypt(jsonObjects, encryKey);
                }
                if (log.outputArgs() && output) {
                    sb.append(jsonObjects);
                    sb.append(',');
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(']');
        return sb.toString();
    }

    /**
     * 方法执行完毕后的日志
     *
     * @param log
     * @param result
     * @param exception
     * @return
     */
    protected final String buildEndLogText(Logable log, Object result, Throwable exception) {
        final StringBuilder sb = new StringBuilder();
        sb.append(SPLIT + "Result: ");
        if (log.outputResult() && result != null) {
        	if(result instanceof Collection) {//返回集合，减少日志起见，仅打印类型和大小
                sb.append(result.getClass()+"@Size:"+((Collection)result).size());
        	}
        	else {
                sb.append(result instanceof String ? result : JSONObject.toJSONString(result));
        	}
        }
        sb.append(SPLIT + "Error: ");
        if (log.outputError() && exception != null) {
            sb.append(exception.getMessage() + EXCEPTION_SPLIT);
            StackTraceElement[] stackTrace = exception.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                String str = StringUtils.toString(stackTraceElement);
                if (str.contains("com.hashtech")) {
                    sb.append(str + EXCEPTION_SPLIT);
                } else {
                    continue;
                }
            }

            Throwable cause = exception.getCause();
            if (cause != null) {
                sb.append(cause.getMessage() + EXCEPTION_SPLIT);
                StackTraceElement[] stackTrace1 = cause.getStackTrace();
                for (StackTraceElement stackTraceElement : stackTrace1) {
                    String str = StringUtils.toString(stackTraceElement);
                    if (str.contains("com.hashtech")) {
                        sb.append(str + EXCEPTION_SPLIT);
                    } else {
                        continue;
                    }
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    
    
    public static AppException getBusinessCause(final Throwable t) {
        if (t == null) {
            return null;
        }
        Throwable cause = t;

        while (cause != null) {
            if (cause instanceof AppException) {
                return (AppException) cause;
            } else {
                cause = cause.getCause();
            }
        }

        return null;
    }

    public static String getReturnCode(final Throwable t) {
    	if(t == null) {
    		return Constants.SUCCESS;
    	}
    	if(t instanceof AppException) {
    		return ((AppException)t).getErrorCode();
    	} else if(t instanceof FeignException) {
    		FeignExceptionDTO fed = FeignExceptionUtils.parseFeignException(t);
    		if(fed == null) {
    			return Constants.ReturnCode.REMOTE_ERROR.code;
    		} else {
    			return fed.getCode();
    		}
    	}
    	if(t.getCause() != null) {
    		return getReturnCode(t.getCause());
    	} else {
    		return Constants.FAIL;
    	}
    }
}
