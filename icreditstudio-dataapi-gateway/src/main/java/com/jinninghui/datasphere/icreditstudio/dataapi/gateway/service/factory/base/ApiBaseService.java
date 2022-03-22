package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.base;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public interface ApiBaseService {
    BusinessResult<Object> getData(String version, String path, Map map, RedisApiInfo apiInfo, ApiLogInfo apiLogInfo, Connection conn, String querySql) throws SQLException;
}
