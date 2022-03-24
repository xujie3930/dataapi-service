package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.base;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.sql.SQLException;
import java.util.Map;

public interface ApiBaseService {
    /**
     *
     * @param map
     * @param apiInfo
     * @param apiLogInfo
     * @param conn
     * @param querySql
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     */
    BusinessResult<Object> getData( Map map, RedisApiInfo apiInfo, ApiLogInfo apiLogInfo) throws SQLException;
}
