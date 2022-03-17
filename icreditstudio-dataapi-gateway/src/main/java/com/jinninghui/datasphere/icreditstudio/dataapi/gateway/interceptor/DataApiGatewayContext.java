package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor
 * ClassName: DataApiGatewayContext
 * Description:  DataApiGatewayContext类
 * Date: 2022/3/17 2:51 下午
 *
 * @author liyanhui
 */
public class DataApiGatewayContext {

    private RedisApiInfo apiInfo;

    public ApiLogInfo getApiLogInfo() {
        return apiLogInfo;
    }

    public void setApiLogInfo(ApiLogInfo apiLogInfo) {
        this.apiLogInfo = apiLogInfo;
    }

    private ApiLogInfo apiLogInfo;

    public RedisApiInfo getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(RedisApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }

}
