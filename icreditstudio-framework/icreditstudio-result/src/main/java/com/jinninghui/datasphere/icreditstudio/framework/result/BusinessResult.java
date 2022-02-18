package com.jinninghui.datasphere.icreditstudio.framework.result;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author liyanhui
 */
public class BusinessResult<T> implements Serializable {

    private static final long serialVersionUID = 9191892693219217387L;

    private static final String RESP_CODE_SUCCESS = "0000";
    private static final String RESP_MSG_SUCCESS = "Success";

    /**
     * 0000表示成功，其他表示失败
     */
    private String returnCode = RESP_CODE_SUCCESS;

    /**
     * 如果result非0000，则 errorMsg 为错误信息， result为0000，errorMsg为空
     */
    private String returnMsg = RESP_MSG_SUCCESS;

    private String nonceStr = UUID.randomUUID().toString().replaceAll("-", "");

    private boolean success;

    private T data;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getReturnCode() {
        return returnCode;
    }


    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }


    public String getReturnMsg() {
        return returnMsg;
    }


    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }


    public String getNonceStr() {
        return nonceStr;
    }


    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success){
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private BusinessResult(){}

    /**
     * 构造返回成功对象结果
     * @param data 结果参数
     * @return result
     */
    public static <T> BusinessResult<T> success(T data) {
        BusinessResult<T> result = new BusinessResult<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    /**
     * 构造失败对象
     * @param code 编码
     * @param message 消息
     * @return result
     */
    public static <T> BusinessResult<T> fail(String code, String message, T data) {
        BusinessResult<T> result = new BusinessResult<>();
        result.setReturnCode(code);
        result.setData(data);
        result.setReturnMsg(message);
        result.setSuccess(false);
        return result;
    }

    /**
     * 构造失败对象
     * @param code 编码
     * @param message 消息
     * @return result
     */
    public static <T> BusinessResult<T> fail(String code, String message) {

        BusinessResult<T> result = new BusinessResult<>();
        result.setReturnCode(code);
        result.setReturnMsg(message);
        result.setSuccess(false);
        result.setData(null);
        return result;
    }
}
