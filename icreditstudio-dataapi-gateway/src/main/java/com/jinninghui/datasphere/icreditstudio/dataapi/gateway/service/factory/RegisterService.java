package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.factory.base.ApiBaseService;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
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
    private KafkaProducer kafkaProducer;

    @Override
    public BusinessResult<Object> getData(Map params, RedisApiInfo apiInfo, ApiLogInfo apiLogInfo) {
        List<RegisterApiParamInfo> registerApiParamInfos = apiInfo.getRegisterApiParamInfoList();
        List<RegisterApiParamInfo> requestList = registerApiParamInfos.stream()
                .filter((RegisterApiParamInfo r) -> RequestFiledEnum.IS_REQUEST_FIELD.getCode().equals(r.getIsRequest()))
                .collect(Collectors.toList());
        List<RegisterApiParamInfo> responseList = registerApiParamInfos.stream()
                .filter((RegisterApiParamInfo r) -> ResponseFiledEnum.IS_RESPONSE_FIELD.getCode().equals(r.getIsRequest()))
                .collect(Collectors.toList());
        //获取所有返回参数
        Set<String> set = new HashSet<>(responseList.stream().map(RegisterApiParamInfo -> RegisterApiParamInfo.getFieldName()).collect(Collectors.toList()));
        //生成入参列表,参数格式:name1=value1&name2=value2
        StringBuilder builder = new StringBuilder();
        for (RegisterApiParamInfo requestParam : requestList) {
            builder.append(requestParam.getFieldName()).append("=").append(StringUtils.isBlank((String) params.get(requestParam.getFieldName()))? requestParam.getDefaultValue() : (String) params.get(requestParam.getFieldName())).append("&");
        }
        String requestParamStr = builder.toString();
        if (!StringUtils.isBlank(requestParamStr)){
            requestParamStr = requestParamStr.substring(0, requestParamStr.length() - 1);
        }
        String requestHttpPre = apiInfo.getReqHost() + apiInfo.getReqPath();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> restResult = restTemplate.getForEntity(StringUtils.isBlank(requestParamStr)? requestHttpPre : requestHttpPre + "?" + requestParamStr, String.class);
        String response = restResult.getBody();
        //返回分别对bean类型和array类型做处理
        JSONObject newJsonObj = new JSONObject();
        if (response.startsWith("[") && response.endsWith("]")){
            JSONArray array = JSON.parseArray(response);
            for (String key : set) {
                for (Object o : array) {
                    newJsonObj.put(key, ((JSONObject)o).get(key));
                }
            }
        }else {
            JSONObject jsonObject = JSON.parseObject(response);
            for (String key : set) {
                newJsonObj.put(key, jsonObject.get(key));
            }
        }
        ApiLogInfo successLog = generateSuccessLog(apiLogInfo, requestHttpPre + requestParamStr);
        kafkaProducer.send(successLog);
        return BusinessResult.success(newJsonObj);
    }

    private ApiLogInfo generateSuccessLog(ApiLogInfo apiLogInfo, String querySql) {
        apiLogInfo.setExecuteSql(querySql);
        apiLogInfo.setCallEndTime(new Date());
        apiLogInfo.setCallStatus(CallStatusEnum.CALL_SUCCESS.getCode());
        apiLogInfo.setRunTime(System.currentTimeMillis() - apiLogInfo.getCallBeginTime().getTime());
        return apiLogInfo;
    }
}
