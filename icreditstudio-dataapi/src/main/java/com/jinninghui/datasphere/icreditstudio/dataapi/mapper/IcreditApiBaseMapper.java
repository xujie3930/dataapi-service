package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiNameAndIdListResult;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * API表基础信息表 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiBaseMapper extends BaseMapper<IcreditApiBaseEntity> {

    IcreditApiBaseEntity findByApiName(@Param("name") String name);

    IcreditApiBaseEntity findByApiPath(@Param("path") String path);

    void updatePublishStatusById(@Param("id") String id, @Param("publishStatus") Integer publishStatus, @Param("nowDate") Date nowDate, @Param("userId") String userId);

    List<ApiNameAndIdListResult> getApiByApiGroupId(List<String> apiGroupIds);

    String findPublishedByWorkFlowId(@Param("workFlowId") String workFlowId);

    List<String> getIdsByApiGroupIds(List<String> apiGroupIds);

    String findPublishedByApiGroupId(@Param("apiGroupId") String apiGroupId);

    void truthDelById(@Param("id") String id);

    Long getCountByPublishAndDelFlag(@Param("publishStatus") Integer publishStatus, @Param("delFlag") Integer delFlag);

    Long newlyDayList(@Param("createTime") Date date);

    Integer queryInnerApiCount(@Param("collection") Collection<String> collection);
}
