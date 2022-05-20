package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

/**
 * @author xujie
 * @description 业务流程返回参数
 * @create 2022-02-22 14:07
 **/
@Data
public class StatisticsAppTopResult {
    //应用ID
    private String appId;
    //引用名称
    private String appName;
    //授权接口个数
    private Integer authApiCount;
    //调用接口次数
    private Integer useApiCount;
}
