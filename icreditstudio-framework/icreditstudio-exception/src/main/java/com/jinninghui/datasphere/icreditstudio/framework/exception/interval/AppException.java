package com.jinninghui.datasphere.icreditstudio.framework.exception.interval;

import com.jinninghui.datasphere.icreditstudio.framework.exception.BaseException;
import com.jinninghui.datasphere.icreditstudio.framework.exception.ExceptionType;

import java.util.HashMap;
import java.util.Map;

public class AppException extends BaseException {
    private static final long serialVersionUID = -8610734771461037783L;

    protected ExceptionParamBuilder builder;

    public AppException(String errorCode) {
        super(errorCode);
    }

    public AppException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
        this.errorCode = errorCode;
        if (errorMsg.length() > 256)
            errorMsg = errorMsg.substring(0, 255);
        this.errorMsg = errorMsg;
    }

    public AppException(String errorCode, ExceptionParamBuilder builder) {
        super(errorCode);
        this.errorCode = errorCode;
        if (builder == null) {
            throw new IllegalArgumentException("builder must not be null");
        }
        this.builder = builder;
    }

    public AppException(String errorCode, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
    }

    public AppException(String errorCode, String errorMsg, Throwable cause) {
        super(errorCode, errorMsg, cause);
        this.errorCode = errorCode;
        if (errorMsg.length() > 256)
            errorMsg = errorMsg.substring(0, 255);
        this.errorMsg = errorMsg;
    }

    public Map<String, Object> getAll() {
        if (builder == null) {
            return new HashMap<>();
        }
        return builder.getAll();
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.APP_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "App Exception";
    }
}
