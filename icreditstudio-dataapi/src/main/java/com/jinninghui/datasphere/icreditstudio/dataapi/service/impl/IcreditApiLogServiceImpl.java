package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiLogEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.LogCallStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiLogMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.LogDetailRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.LogListQueryRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.LogDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.LogListQueryResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.springframework.beans.BeanUtils;
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
        if(!StringUtils.isEmpty(request.getCallBeginTime())) {
            request.setCallBeginTime(String.valueOf(new StringBuffer(request.getCallBeginTime()).append(" 00:00:00")));
        }
        if(!StringUtils.isEmpty(request.getCallEndTime())) {
            request.setCallEndTime(String.valueOf(new StringBuffer(request.getCallEndTime()).append(" 59:59:59")));
        }
        request.setPageStartNum((request.getPageNum() - 1) * request.getPageSize());
        Long logCount = apiLogMapper.countLog(request);
        List<LogListQueryResult> logList = apiLogMapper.getList(request);
        return BusinessResult.success(BusinessPageResult.build(logList, request, logCount));
    }

    @Override
    public BusinessResult<LogDetailResult> detail(LogDetailRequest request) {
        if(StringUtils.isEmpty(request.getId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_00000001.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_00000001.getMessage());
        }
        IcreditApiLogEntity logEntity = apiLogMapper.selectById(request.getId());
        LogDetailResult logDetailResult = new LogDetailResult();
        BeanUtils.copyProperties(logEntity, logDetailResult);
        logDetailResult.setCallBeginTime(logEntity.getCallBeginTime().getTime());
        if(LogCallStatusEnum.SUCCEED.getCode().equals(logEntity.getCallStatus()) || LogCallStatusEnum.FAILED.getCode().equals(logEntity.getCallStatus())) {
            logDetailResult.setCallEndTime(logEntity.getCallEndTime().getTime());
        }
        return BusinessResult.success(logDetailResult);
    }
}
