package com.jinninghui.datasphere.icreditstudio.framework.exception.interval;

import com.jinninghui.datasphere.icreditstudio.framework.exception.BaseException;
import com.jinninghui.datasphere.icreditstudio.framework.exception.ExceptionType;

/**
 * @author liyanhui
 */
public class DataAccessException extends BaseException {
    private static final long serialVersionUID = -1219262335729891920L;

    /**
     * 构造方法
     * @param message
     */
    public DataAccessException(final String message) {
        super(message);
    }

    /**
     * 构造方法
     * @param cause
     */
    public DataAccessException(final Throwable cause) {
        super("DataAccessError", cause);
    }

    /**
     * 构造方法
     * @param message
     * @param cause
     */
    public DataAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.DATA_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "Data Exception";
    }
}

