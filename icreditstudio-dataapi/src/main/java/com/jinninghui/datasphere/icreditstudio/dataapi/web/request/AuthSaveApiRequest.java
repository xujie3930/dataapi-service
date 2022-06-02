package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

/**
 * @author xujie
 * @description 应用授权API,保存参数
 * @create 2022-02-25 15:08
 **/
public class AuthSaveApiRequest {
    //应用主键id
    @NotBlank(message = "20000021")
    private String apiId;
    //API的主键id
    private Set<String> appIds;
    //有效起始时间(-1表示无穷)
    private Long periodBegin;
    //有效结束时间(-1表示无穷)
    private Long periodEnd;
    //允许调用次数(-1表示无穷)
    private Integer allowCall;
    private String durationType;//可调用次数类型，0--有限次，1--无限次

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public Set<String> getAppIds() {
        return appIds;
    }

    public void setAppIds(Set<String> appIds) {
        this.appIds = appIds;
    }

    public Long getPeriodBegin() {
        return periodBegin;
    }

    public void setPeriodBegin(Long periodBegin) {
        this.periodBegin = periodBegin;
    }

    public Long getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(Long periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Integer getAllowCall() {
        return allowCall;
    }

    public void setAllowCall(Integer allowCall) {
        this.allowCall = allowCall;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }
}
