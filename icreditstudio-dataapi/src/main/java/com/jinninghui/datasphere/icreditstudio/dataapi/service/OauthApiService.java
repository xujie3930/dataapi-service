package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.InternalUserInfoVO;

import java.util.List;
import java.util.Map;

public interface OauthApiService {

    InternalUserInfoVO getUserById(String userId);

    Map<String, String> getUserNameBatch(List<String> userIds);
}
