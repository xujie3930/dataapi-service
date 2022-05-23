package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.LogListQueryRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.LogListQueryResult;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

    /**
     * 通过APPID查询使用次数
     * @author  maoc
     * @create  2022/5/20 14:25
     * @desc
     **/
    List<Map<String, Object>> queryUsedCountByAppIds(@Param("list") Collection<String> list);
}
