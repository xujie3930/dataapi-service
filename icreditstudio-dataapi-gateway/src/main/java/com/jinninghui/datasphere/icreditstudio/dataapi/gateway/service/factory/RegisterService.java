package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.base.ApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.HttpUtils;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xujie
 * @description 注册api
 * @create 2022-02-24 14:35
 **/
@Service
public class RegisterService implements ApiBaseService {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public BusinessResult<Object> getData(String version, String path, Map map, RedisApiInfo apiInfo, ApiLogInfo apiLogInfo, Connection conn, String querySql) throws SQLException {
        List<RegisterApiParamInfo> registerApiParamInfos = apiInfo.getRegisterApiParamInfoList();
        List<RegisterApiParamInfo> requestList = registerApiParamInfos.stream()
                .filter((RegisterApiParamInfo r) -> RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(r.getIsRequest()))
                .collect(Collectors.toList());
        List<RegisterApiParamInfo> responseList = registerApiParamInfos.stream()
                .filter((RegisterApiParamInfo r) -> ResponseFiledEnum.IS_RESPONSE_FIELD.getCode().equals(r.getIsRequest()))
                .collect(Collectors.toList());
        //生成入参列表,参数格式:name1=value1&name2=value2
        StringBuilder builder = new StringBuilder();
        for (RegisterApiParamInfo requestParam : requestList) {
            builder.append(requestParam.getFieldName()).append("=").append(StringUtils.isBlank((String) map.get(requestParam.getFieldName()))? requestParam.getDefaultValue() : (String) map.get(requestParam.getFieldName())).append("&");
        }
        String requestParamStr = builder.toString();
        requestParamStr = requestParamStr.substring(0, requestParamStr.length() - 1);
        String requestHttpPre = apiInfo.getReqHost() + apiInfo.getReqPath();
        String response = HttpUtils.sendGet(requestHttpPre, requestParamStr);
        ApiLogInfo successLog = generateSuccessLog(apiLogInfo, requestHttpPre + requestParamStr);
        kafkaProducer.send(successLog);
        return BusinessResult.success(response);
    }

    private ApiLogInfo generateSuccessLog(ApiLogInfo apiLogInfo, String querySql) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_SUCCESS.getCode());
        apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        return apiLogInfo;
    }
}
