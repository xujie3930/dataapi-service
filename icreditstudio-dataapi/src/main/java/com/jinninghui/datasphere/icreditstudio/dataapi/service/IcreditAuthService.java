package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiSaveResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthInfoResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthListResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
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

    /**
     * 改为：前端只传新增的api列表（已新增的置灰不可删除）
     * @author  maoc
     * @create  2022/6/10 11:21
     * @desc
     **/
    BusinessResult<Boolean> saveDefV2(String userId, AuthSaveRequest request);
    BusinessResult<Boolean> saveDef(String userId, AuthSaveRequest request);
    BusinessResult<Boolean> configDef(String userId, AuthSaveRequest request);
    /**
     * 编辑接口时，调用此方法绑定接口和应用的授权关系
     * 此接口整合了接口发布方法（避免分布式事务问题），故属于长事务接口
     * @author  maoc
     * @create  2022/6/7 10:41
     * @desc
     **/
    BusinessResult<ApiSaveResult> saveOuterApiDef(String userId, AuthSaveApiRequest request);

    List<IcreditAuthEntity> findByAppId(String appId);

    BusinessResult<AuthInfoResult> authInfo(AuthInfoRequest request);

    /**
     * 分页列表
     * @author  maoc
     * @create  2022/6/1 16:38
     * @desc
     **/
    BusinessPageResult<AuthListResult> authList(AuthListRequest request);
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
