package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.CallStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.RedisApiInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor.DataApiGatewayContextHolder;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.OpenApiService;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.ApiFactory;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.base.ApiBaseService;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.impl
 * ClassName: OpenApiServiceImpl
 * Description:  OpenApiServiceImpl类
 * Date: 2022/3/17 3:08 下午
 *
 * @author liyanhui
 */
@Slf4j
@Service
public class OpenApiServiceImpl implements OpenApiService {

    @Autowired
    private KafkaProducer kafkaProducer;
    @Resource
    private ApiFactory apiFactory;

    @Override
    public BusinessResult<Object> getData(String version, String path, Map map) {
        String querySql = null;
        RedisApiInfo apiInfo = DataApiGatewayContextHolder.get().getApiInfo();
        ApiLogInfo apiLogInfo = DataApiGatewayContextHolder.get().getApiLogInfo();
        try {
            ApiBaseService apiService = apiFactory.getApiService(apiInfo, apiLogInfo);
            return apiService.getData(map, apiInfo, apiLogInfo);
        } catch (Exception e) {
            e.printStackTrace();
            //发送kafka失败信息
            ApiLogInfo failLog = generateFailLog(apiLogInfo, querySql, e);
            kafkaProducer.send(failLog);
            log.error("发送kafka异常日志:{}", failLog);
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000013.getCode(), failLog.getExceptionDetail());
        } finally {
            DataApiGatewayContextHolder.remove();
        }
    }




    private ApiLogInfo generateFailLog(ApiLogInfo apiLogInfo, String querySql, Exception e) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_FAIL.getCode());
        if (null != apiLogInfo.getCallBeginTime()) {
            apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        }
        if (e instanceof AppException) {
            try {
                Field errorMsg = e.getClass().getSuperclass().getDeclaredField("errorMsg");
                ReflectionUtils.makeAccessible(errorMsg);
                String errorLog = (String) errorMsg.get(e);
                apiLogInfo.setExceptionDetail(errorLog);
            } catch (Exception exception) {
                exception.printStackTrace();
                apiLogInfo.setExceptionDetail(exception.toString());
            }
        } else {
            apiLogInfo.setExceptionDetail(e.toString());
        }
        return apiLogInfo;
    }
}
