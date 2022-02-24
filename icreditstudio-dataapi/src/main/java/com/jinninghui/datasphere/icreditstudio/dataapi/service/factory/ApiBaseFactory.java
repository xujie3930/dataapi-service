package com.jinninghui.datasphere.icreditstudio.dataapi.service.factory;

import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.ApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.impl.ApiBaseGenerateService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.impl.ApiBaseRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xujie
 * @description 数据源生成API
 * @create 2022-02-24 15:32
 **/
@Service
public class ApiBaseFactory {

    @Autowired
    private ApiBaseGenerateService apiBaseGenerateService;
    @Autowired
    private ApiBaseRegisterService apiBaseRegisterService;

    public ApiBaseService getApiService(Integer type) {
        ApiTypeEnum apiTypeEnum = ApiTypeEnum.findByType(type);
        switch (apiTypeEnum) {
            case API_REGISTER:
                return apiBaseRegisterService;
            case API_GENERATE:
                return apiBaseGenerateService;
            default:
                return apiBaseGenerateService;
        }
    }
}
