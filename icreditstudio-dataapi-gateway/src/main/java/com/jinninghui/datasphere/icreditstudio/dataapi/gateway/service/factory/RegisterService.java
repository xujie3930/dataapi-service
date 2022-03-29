package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.base.ApiBaseService;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xujie
 * @description 注册api
 * @create 2022-02-24 14:35
 **/
@Service
public class RegisterService implements ApiBaseService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Override
    public BusinessResult<Object> getData(Map params, RedisApiInfo apiInfo, ApiLogInfo apiLogInfo) {
        List<RegisterApiParamInfo> registerApiParamInfos = apiInfo.getRegisterApiParamInfoList();
        List<RegisterApiParamInfo> requestList = registerApiParamInfos.stream()
                .filter((RegisterApiParamInfo r) -> RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(r.getIsRequest()))
                .collect(Collectors.toList());
//        List<RegisterApiParamInfo> responseList = registerApiParamInfos.stream()
//                .filter((RegisterApiParamInfo r) -> ResponseFiledEnum.IS_RESPONSE_FIELD.getCode().equals(r.getIsResponse()))
//                .collect(Collectors.toList());
        //生成入参列表,参数格式:name1=value1&name2=value2
        StringBuilder builder = new StringBuilder();
        if (!CollectionUtils.isEmpty(requestList)){
            for (RegisterApiParamInfo requestParam : requestList) {
                if (StringUtils.isBlank((String) params.get(requestParam.getFieldName())) && StringUtils.isBlank(requestParam.getDefaultValue())){
                    continue;
                }
                builder.append(requestParam.getFieldName()).append("=").append(StringUtils.isBlank((String) params.get(requestParam.getFieldName()))? requestParam.getDefaultValue() : (String) params.get(requestParam.getFieldName())).append("&");
            }
        }
        String requestParamStr = builder.toString();
        if (!StringUtils.isBlank(requestParamStr)){
            requestParamStr = requestParamStr.substring(0, requestParamStr.length() - 1);
        }
        String requestHttpPre = apiInfo.getReqHost() + apiInfo.getReqPath();
        String requestUrl = StringUtils.isBlank(requestParamStr)? requestHttpPre : requestHttpPre + "?" + requestParamStr;
        ResponseEntity<String> restResult = restTemplate.getForEntity(requestUrl, String.class);
        String response = restResult.getBody();
        //返回分别对bean类型和array类型做处理
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;
        if (response.startsWith("[") && response.endsWith("]")){
            jsonArray = JSON.parseArray(response);
        }else {
            jsonObject = JSON.parseObject(response);
        }
        ApiLogInfo successLog = generateSuccessLog(apiLogInfo, requestHttpPre + requestParamStr);
        kafkaProducer.send(successLog);
        return BusinessResult.success(jsonArray == null? jsonObject : jsonArray);
    }

    private ApiLogInfo generateSuccessLog(ApiLogInfo apiLogInfo, String querySql) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_SUCCESS.getCode());
        apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        return apiLogInfo;
    }
}
