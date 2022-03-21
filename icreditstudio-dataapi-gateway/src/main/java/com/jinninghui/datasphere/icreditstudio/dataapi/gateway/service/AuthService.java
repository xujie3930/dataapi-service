package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service;

import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 主题资源表 服务类
 * </p>
 *
 * @author xujie
 * @since 2021-11-23
 */
public interface AuthService {

    BusinessResult<String> getToken(String generateId, String secretContent);

}
