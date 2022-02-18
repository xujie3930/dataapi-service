package com.jinninghui.datasphere.icreditstudio.framework.log;

import com.jinninghui.datasphere.icreditstudio.framework.utils.AesUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.UUIDUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequestWrapper;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述 ：
 *
 * @author lidab
 * @date 2018/2/12.
 */
public class LogUtil {
	private final static ThreadLocal<String> transactionNoInThread = new ThreadLocal<String>();
    private final static Map<String, Logger> LOGGERS = new HashMap<String, Logger>();

    /**
     * 获取 transaction
     *
     * @return
     */
    public static String getTransaction(Logable log) {
    	String transactionNo = null;
    	//from attrs
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attrs = null ;
            if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
                attrs = (ServletRequestAttributes) requestAttributes;
            }
            if(attrs != null) {
                transactionNo = (String) attrs.getRequest().getAttribute("transactionNo");
                if(StringUtils.isEmpty(transactionNo)) {
                	transactionNo = UUIDUtils.uuid();
                	attrs.setAttribute("transactionNo", transactionNo, RequestAttributes.SCOPE_REQUEST);
                }
            }
        } catch (Exception e) { }
        //from thread context
        if(StringUtils.isEmpty(transactionNo) && log != null) {
        	transactionNo = transactionNoInThread.get();
        	if(transactionNo==null) {
        		transactionNo = UUIDUtils.uuid();
        		transactionNoInThread.set(transactionNo);
        	} 
        }
        return transactionNo;
    }

    /**
     * 加密字段
     *
     * @param str
     * @param key
     * @param encryKey
     * @return
     */
    public static String encrypt(String str, String key, String encryKey) {
        if (org.apache.commons.lang3.StringUtils.isBlank(str) || org.apache.commons.lang3.StringUtils.isBlank(key)) {
            return null;
        }
        if (!org.apache.commons.lang3.StringUtils.contains(str, key)) {
            return null;
        }
        String subString = org.apache.commons.lang3.StringUtils.substringAfter(str, key);
        if (org.apache.commons.lang3.StringUtils.isBlank(subString)) {
            return null;
        }
        String[] strings = org.apache.commons.lang3.StringUtils.substringsBetween(subString, ":\"", "\"");
        if (strings == null || strings.length == 0) {
            return null;
        }
        String str0 = strings[0];
        if (org.apache.commons.lang3.StringUtils.isBlank(str0)) {
            return null;
        }
        String encryptStr = null;
        try {
            encryptStr = AesUtils.encrypt(str0, encryKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}
        if (org.apache.commons.lang3.StringUtils.isBlank(encryptStr)) {
            return null;
        }
        return org.apache.commons.lang3.StringUtils.replace(str, str0, encryptStr);
    }

    /**
     * 获取参数
     *
     * @param args
     * @return
     */
    public static List<Object> args(Object[] args) {
        if (args == null) {
            return null;
        }
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                objects.add(null);
            } else {
                String packageName;
                if (args[i] instanceof Object[]) {
                    packageName = args[i].getClass().getClass().getPackage().getName();
                } else {
                    packageName = args[i].getClass().getPackage().getName();
                }
                boolean flag = (packageName.startsWith("java.lang") || packageName.startsWith("java.util") || packageName.startsWith("com.hashtech"))
                        && !(args[i] instanceof HttpServletRequestWrapper);
                if (flag) {
                    objects.add(args[i]);
                } else {
                    objects.add(args[i] != null ? args[i].getClass().getName() : "NULL");
                }
            }
        }
        return objects;
    }

    /**
     * 加密字符串
     *
     * @param jsonString
     * @param encrypts
     * @return
     */
	public static String encrypt(String jsonString, String[] encrypts, String encryKey) {
		for (int i = 0; i < encrypts.length; i++) {
			String encrypt = encrypt(jsonString, encrypts[i], encryKey);
			if (org.apache.commons.lang3.StringUtils.isNotBlank(encrypt)) {
				return encrypt;
			}
		}
		return jsonString;
	}

    /**
     * 获取注解的方法
     *
     * @param pjp
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public static Method getCurrentMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException, SecurityException {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        return methodSignature.getMethod();
    }


    /**
     * 根据loggerName，获取Logger对象
     *
     * @param loggerName
     * @return
     */
    public static Logger getLogger(String loggerName) {
        if (LOGGERS.containsKey(loggerName)) {
            return LOGGERS.get(loggerName);
        } else {
            synchronized (loggerName) {
                Logger log = LoggerFactory.getLogger(loggerName);
                LOGGERS.put(loggerName, log);
                return log;
            }
        }
    }

    public static String encrypt(String str, String encryKey) {
        String encryptStr = null;
        try {
            encryptStr = AesUtils.encrypt(str, encryKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptStr;
    }
}
