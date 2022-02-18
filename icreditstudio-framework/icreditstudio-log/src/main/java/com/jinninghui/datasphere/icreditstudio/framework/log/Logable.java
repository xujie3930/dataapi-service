package com.jinninghui.datasphere.icreditstudio.framework.log;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface Logable {
    String loggerName() default "monitor";

    String businessTag() default "";

    Format format() default Format.TEXT;

    Level level() default Level.INFO;
    
    String errorCode() default "";

    boolean outputArgs() default true;

    int[] ignoreOutputArgsIndex() default {};

    boolean outputResult() default true;

    boolean outputError() default true;

    boolean encrypt() default false;

    int[] encryptArgsIndex() default {};

    boolean showAll() default false;
    /*
     * 该方法是否是入口，对于Request=null时，如果指定该方法为入口，则在该方法处生成transactionNo，多用在JOB中
     * transacionNo是用于跟踪一个调用栈的
     */
    boolean isEntrance() default false;		

    /**
     * 如果是查询类方法，不需要输出响应值，可设置为QUERY，则日志记录中不会记录其响应内容，避免大规模查询（例如界面的查询批量订单）
     * 输出过多无用日志
     * @return
     */
    MethodType methodType() default MethodType.ACTION;
    
    enum Format {
        JSON,
        TEXT
    }
    
    enum MethodType {
    	QUERY,
    	ACTION
    }

    enum Level {
        INFO,
        DEBUG
    }
}
