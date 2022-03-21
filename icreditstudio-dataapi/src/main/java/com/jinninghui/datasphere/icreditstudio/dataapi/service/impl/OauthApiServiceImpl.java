package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.feign.MicroOauth2ApiFeignClient;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.CommonResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.InternalUserInfoVO;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.OauthApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author xujie
 * @description 获取iframe用户信息
 * @create 2022-01-18 23:35
 **/
@Service
public class OauthApiServiceImpl implements OauthApiService {
    @Autowired
    private MicroOauth2ApiFeignClient microOauth2ApiFeignClient;

    @Override
    public InternalUserInfoVO getUserById(String userId) {
        CommonResult<InternalUserInfoVO> result = microOauth2ApiFeignClient.info(userId);
        if (Objects.isNull(result.getData())) {
            return null;
        }
        return result.getData();
    }

    @Override
    public Map<String, String> getUserNameBatch(List<String> userIds) {
        CommonResult<Map<String, String>> result = microOauth2ApiFeignClient.selUserBatch(userIds);
        Map<String, String> map = result.getData();
        return map;
    }
}
