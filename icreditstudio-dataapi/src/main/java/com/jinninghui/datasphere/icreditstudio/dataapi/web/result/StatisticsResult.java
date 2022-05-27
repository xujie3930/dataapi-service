package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

/**
 * @author xujie
 * @description 业务流程返回参数
 * @create 2022-02-22 14:07
 **/
@Data
public class StatisticsResult {
    //接口总数量
    private Long interfaceCount;
    //“已发布”状态接口总量
    private Long publishInterfaceCount;
    //授权给应用的接口总数量
    private Long authInterfaceCount;
    //授权应用数量
    private Long appAuthCount;
    //启用的应用数量
    private Long enableAppCount;
    //应用总数量
    private Long appCount;
    //当日新增接口数量
    private Long newlyInterfaceCount;
}
