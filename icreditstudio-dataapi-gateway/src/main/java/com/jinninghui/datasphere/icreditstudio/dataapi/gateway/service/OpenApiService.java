package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service;

import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.util.Map;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service
 * ClassName: OpenApiService
 * Description:  OpenApiService类
 * Date: 2022/3/17 3:07 下午
 *
 * @author liyanhui
 */
public interface OpenApiService {

    BusinessResult<Object> getData(String version, String path, Map map);

}
