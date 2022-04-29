package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppEnableEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiPublishStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.StatisticsService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xujie
 * @description 统计服务类
 * @create 2022-04-29 15:45
 **/
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private IcreditApiBaseMapper apiBaseMapper;
    @Autowired
    private IcreditAuthMapper authMapper;
    @Autowired
    private IcreditAppMapper appMapper;
    @Override
    public StatisticsResult statistics() {
        StatisticsResult result = new StatisticsResult();
        Long publishInterfaceCount = apiBaseMapper.getCountByPublishAndDelFlag(ApiPublishStatusEnum.PUBLISHED.getCode(), DelFlagEnum.ENA_BLED.getCode());
        result.setPublishInterfaceCount(publishInterfaceCount);
        Long interfaceCount = apiBaseMapper.getCountByPublishAndDelFlag(null, DelFlagEnum.ENA_BLED.getCode());
        result.setInterfaceCount(interfaceCount);
        Long authInterfaceCount = authMapper.getApiAuthCount(DelFlagEnum.ENA_BLED.getCode());
        result.setAuthInterfaceCount(authInterfaceCount);
        Long appAuthCount = authMapper.getAppAuthCount(DelFlagEnum.ENA_BLED.getCode());
        result.setAppAuthCount(appAuthCount);
        Long enableAppCount = appMapper.getCountByEnableAndDelFlag(AppEnableEnum.ENABLE.getCode(), DelFlagEnum.ENA_BLED.getCode());
        result.setEnableAppCount(enableAppCount);
        Long appCount = appMapper.getCountByEnableAndDelFlag(null, DelFlagEnum.ENA_BLED.getCode());
        result.setAppCount(appCount);
        return result;
    }
}
