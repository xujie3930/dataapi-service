package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

/**
 * @author maoshi
 * @description 业务流程返回参数
 * @create 2022-02-22 14:07
 **/
public class StatisticsApiTopResult {
    //api ID
    private String apiId;
    //api 名称
    private String apiName;
    //接口来源
    private Integer apiSource;
    //调用接口次数
    private Integer apiUsedCount;
    private Integer apiType;
    private Integer sort;

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public Integer getApiSource() {
        return apiSource;
    }

    public void setApiSource(Integer apiSource) {
        this.apiSource = apiSource;
    }

    public Integer getApiUsedCount() {
        return apiUsedCount;
    }

    public void setApiUsedCount(Integer apiUsedCount) {
        this.apiUsedCount = apiUsedCount;
    }

    public Integer getApiType() {
        return apiType;
    }

    public void setApiType(Integer apiType) {
        this.apiType = apiType;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
