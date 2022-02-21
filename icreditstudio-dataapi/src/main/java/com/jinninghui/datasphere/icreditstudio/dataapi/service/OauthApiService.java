package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.InternalUserInfoVO;

public interface OauthApiService {

    InternalUserInfoVO getUserById(String userId);
}
