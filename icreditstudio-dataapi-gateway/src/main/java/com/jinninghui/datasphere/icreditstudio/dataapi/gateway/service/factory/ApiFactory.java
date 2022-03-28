package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.base.ApiBaseService;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author xujie
 * @description 数据源生成API
 * @create 2022-02-24 15:32
 **/
@Service
public class ApiFactory {

    @Autowired
    private GenerateService generateService;
    @Autowired
    private RegisterService registerService;

    public ApiBaseService getApiService(RedisApiInfo apiInfo) {
        if (StringUtils.isBlank(apiInfo.getReqHost()) && StringUtils.isBlank(apiInfo.getReqPath())){
            return generateService;
        }
        return registerService;
    }
}
