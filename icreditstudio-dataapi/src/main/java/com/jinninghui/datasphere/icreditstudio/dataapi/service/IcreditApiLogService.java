package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.LogDetailRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.LogListQueryRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.LogDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.LogListQueryResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

/**
 * <p>
 * 调用日志 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiLogService extends IService<IcreditApiLogEntity> {

    BusinessResult<BusinessPageResult<LogListQueryResult>> getList(LogListQueryRequest request);

    BusinessResult<LogDetailResult> detail(LogDetailRequest request);
}
