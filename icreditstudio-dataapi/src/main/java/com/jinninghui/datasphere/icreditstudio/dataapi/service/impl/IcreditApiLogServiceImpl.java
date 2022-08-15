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
import com.jinninghui.datasphere.icreditstudio.framework.utils.RedisUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private final AtomicBoolean LOG_TOTAL_UPDATE = new AtomicBoolean(true);
    private final String LOG_TOTAL_COUNT="log.total.count";
    @Autowired
    private RedisUtils redisUtils;
    @Resource
    private IcreditApiLogMapper apiLogMapper;

    @Override
    public BusinessResult<BusinessPageResult<LogListQueryResult>> getList(LogListQueryRequest request) {
        if(!StringUtils.isEmpty(request.getCallBeginTime())) {
            request.setCallBeginTime(String.valueOf(new StringBuffer(request.getCallBeginTime()).append(" 00:00:00")));
        }
        if(!StringUtils.isEmpty(request.getCallEndTime())) {
            request.setCallEndTime(String.valueOf(new StringBuffer(request.getCallEndTime()).append(" 23:59:59")));
        }
        request.setPageStartNum((request.getPageNum() - 1) * request.getPageSize());
        //Long logCount = apiLogMapper.countLog(request);
        Long logCount = getLogCount(request);
        List<LogListQueryResult> logList = apiLogMapper.getList(request);
        return BusinessResult.success(BusinessPageResult.build(logList, request, logCount));
    }

    private Long getLogCount(LogListQueryRequest request){
        String callBeginTime = request.getCallBeginTime();
        String callEndTime = request.getCallEndTime();
        Integer callStatus = request.getCallStatus();
        String appName = request.getAppName();
        String apiName = request.getApiName();
        Integer apiVersion = request.getApiVersion();
        if(!StringUtils.isEmpty(callBeginTime) || !StringUtils.isEmpty(callEndTime) || !StringUtils.isEmpty(appName) ||
                !StringUtils.isEmpty(apiName) || null!=apiVersion || null!=callStatus){
            return apiLogMapper.countLog(request);
        }
        //只缓存查询全部的情况
        Boolean update = false;
        Long logCount = 0l;
        Map<String, Object> redisData = redisUtils.hgetall(LOG_TOTAL_COUNT);
        if(null==redisData || redisData.isEmpty()){
            synchronized (LOG_TOTAL_COUNT){
                Map<String, Object> redisData2 = redisUtils.hgetall(LOG_TOTAL_COUNT);
                if(null==redisData2 || redisData2.isEmpty()){
                    final Map<String, String> map = new HashMap<>(4);
                    logCount = apiLogMapper.countLog(request);
                    map.put("logCount", null==logCount?"0":logCount+"");
                    map.put("logExpire", System.currentTimeMillis()+"");
                    redisUtils.hmset(LOG_TOTAL_COUNT, map);
                    return logCount;
                }else{
                    logCount = Long.valueOf((String)redisData2.get("logCount"));
                    Long logExpire = Long.valueOf((String) redisData2.get("logExpire"));
                    if(System.currentTimeMillis()-logExpire.longValue()>60000){
                        update=true;
                    }

                }
            }
        }else{
            logCount = Long.valueOf((String)redisData.get("logCount"));
            Long logExpire = Long.valueOf((String) redisData.get("logExpire"));
            if(System.currentTimeMillis()-logExpire.longValue()>300000){
                update=true;
            }
        }

        //异步更新
        if(update && LOG_TOTAL_UPDATE.compareAndSet(true, false)){
            //默认为可重入锁
            //LOG_TOTAL_UPDATE.set(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        final Map<String, String> map = new HashMap<>(4);
                        final Long logCount = apiLogMapper.countLog(request);
                        map.put("logCount", null==logCount?"0":(logCount)+"");
                        map.put("logExpire", System.currentTimeMillis()+"");
                        redisUtils.hmset(LOG_TOTAL_COUNT, map);
                    }finally {
                        LOG_TOTAL_UPDATE.set(true);
                    }
                }
            }).start();

        }

        return logCount;
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
