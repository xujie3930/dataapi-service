package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseHiEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiBaseHiDetailRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiHistoryListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiHistoryListResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiSaveResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

/**
 * <p>
 * API表基础信息历史版本表 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiBaseHiService extends IService<IcreditApiBaseHiEntity> {

    IcreditApiBaseHiEntity findByApiBaseId(String apiId);

    BusinessResult<BusinessPageResult<ApiHistoryListResult>> getList(ApiHistoryListRequest request);

    BusinessResult<ApiDetailResult> info(ApiBaseHiDetailRequest request);

    BusinessResult<ApiSaveResult> updateApi(String userId, DatasourceApiSaveParam param);
}
