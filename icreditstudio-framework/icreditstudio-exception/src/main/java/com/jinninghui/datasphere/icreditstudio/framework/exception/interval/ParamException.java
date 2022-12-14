package com.jinninghui.datasphere.icreditstudio.framework.exception.interval;

import com.jinninghui.datasphere.icreditstudio.framework.exception.BaseException;
import com.jinninghui.datasphere.icreditstudio.framework.exception.ExceptionType;

/**
 * @author liyanhui
 */
public class ParamException extends BaseException {
    private static final long serialVersionUID = -1L;

    public ParamException(String errorCode, String errorMsg) {
        super(errorCode);
        this.errorCode = errorCode;
        if (errorMsg.length() > 256)
            errorMsg = errorMsg.substring(0, 255);
        this.errorMsg = errorMsg;
    }

    @Override
    public ExceptionType defExceptionType() {
        return ExceptionType.PARAM_EXCEPTION;
    }

    @Override
    public String defShortName() {
        return "Param Exception";
    }

}

