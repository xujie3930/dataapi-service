package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiLogEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiLogMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.LogListQueryRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.LogListQueryResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 调用日志 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditApiLogServiceImpl extends ServiceImpl<IcreditApiLogMapper, IcreditApiLogEntity> implements IcreditApiLogService {

    @Resource
    private IcreditApiLogMapper apiLogMapper;

    @Override
    public BusinessResult<BusinessPageResult<LogListQueryResult>> getList(LogListQueryRequest request) {
        request.setPageStartNum((request.getPageNum() - 1) * request.getPageSize());
        Long logCount = apiLogMapper.countLog(request);
        List<LogListQueryResult> logList = apiLogMapper.getList(request);
        return BusinessResult.success(BusinessPageResult.build(logList, request, logCount));
    }
}
