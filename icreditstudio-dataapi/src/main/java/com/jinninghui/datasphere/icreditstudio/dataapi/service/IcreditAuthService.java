package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthInfoResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthListResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.util.List;

/**
 * <p>
 * 授权表 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAuthService extends IService<IcreditAuthEntity> {

    BusinessResult<Boolean> saveDef(String userId, AuthSaveRequest request);
    BusinessResult<Boolean> saveApiDef(String userId, AuthSaveApiRequest request);

    List<IcreditAuthEntity> findByAppId(String appId);

    BusinessResult<AuthInfoResult> authInfo(AuthInfoRequest request);

    /**
     * 分页列表
     * @author  maoc
     * @create  2022/6/1 16:38
     * @desc
     **/
    List<AuthListResult> authList(AuthListRequest request);
    /**
     * 通过api path查询授权列表
     * @author  maoc
     * @create  2022/6/2 11:46
     * @desc
     **/
    List<AuthListResult> queryApiAuthListByPath(String path);

    /**
     * 批量删除
     * @author  maoc
     * @create  2022/6/2 10:39
     * @desc
     **/
    BusinessResult<Boolean> del(String userId, AuthDelRequest request);

}
