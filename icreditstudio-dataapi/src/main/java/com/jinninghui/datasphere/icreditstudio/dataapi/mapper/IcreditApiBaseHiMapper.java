package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseHiEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiHistoryListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiHistoryListResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * API表基础信息历史版本表 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiBaseHiMapper extends BaseMapper<IcreditApiBaseHiEntity> {

    IcreditApiBaseHiEntity findByApiBaseId(@Param("apiId") String apiId);

    List<ApiHistoryListResult> getList(ApiHistoryListRequest request);

    Long countApiBaseHi(ApiHistoryListRequest request);

    void deleteByEntity(IcreditApiBaseHiEntity entity);

    /**
     * 获取该api所有版本，按版本号降序
     * @param apiId
     * @return
     */
    List<IcreditApiBaseHiEntity> listByApiBaseId(@Param("apiId") String apiId);

    void removeByApiBaseId(@Param("id") String id);

    List<IcreditApiBaseHiEntity> findWaitPublishedByApiId(@Param("apiId") String apiId);

    List<IcreditApiBaseHiEntity> findByApiBaseIdAndName(@Param("apiId") String apiId, @Param("name") String name);
}
