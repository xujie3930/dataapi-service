package com.jinninghui.datasphere.icreditstudio.framework.exception.interval.feign;

/**
 * 描述 ：
 *
 * @author lidab
 * @date 2018/3/27.
 */
public class FeignExceptionDTO {

    private String code;
    private String cause;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
