package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiNameAndIdListResult;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    /**
     * 查询接口调用量列表
     * @author  maoc
     * @create  2022/6/22 15:29
     * @desc
     **/
    List<Map<String, Object>> queryApiInBiUsedCount(final Map<String, Object> paramsMap);
}
