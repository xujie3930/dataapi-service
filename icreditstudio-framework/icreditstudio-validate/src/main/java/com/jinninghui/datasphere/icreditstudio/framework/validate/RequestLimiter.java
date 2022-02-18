package com.jinninghui.datasphere.icreditstudio.framework.validate;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 令牌桶注解实现
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestLimiter {
    /**
     * 每秒创建令牌个数，默认:10
     */
    double QPS() default 10D;

    /**
     * 获取令牌等待超时时间 默认:500
     */
    long timeout() default 500;

    /**
     * 超时时间单位 默认:毫秒
     */
    TimeUnit timeunit() default TimeUnit.MILLISECONDS;

    /**
     * 无法获取令牌返回提示信息
     */
    String msg() default "请稍后再试!";
}
