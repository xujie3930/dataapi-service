package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.LogListQueryRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.LogListQueryResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 调用日志 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiLogMapper extends BaseMapper<IcreditApiLogEntity> {

    Long countLog(LogListQueryRequest request);

    List<LogListQueryResult> getList(LogListQueryRequest request);

    IcreditApiLogEntity findByTraceId(@Param("traceId") String traceId);
}
