package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGroupResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.GroupIdAndNameResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * API分组 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiGroupMapper extends BaseMapper<IcreditApiGroupEntity> {

    Boolean hasExit(@Param("name") String name, @Param("id") String id);

    Integer getMaxSort(@Param("workId") String workId);

    List<ApiGroupResult> getByWorkId(@Param("workFlowId") String workFlowId);

    List<IcreditApiGroupEntity> searchFromName(@Param("name") String name);

    List<GroupIdAndNameResult> getGroupListByWorkFlowId(List<String> workFlowIds);

    void renameById(@Param("name") String name, @Param("id") String id);

    List<String> getIdsByWorkId(@Param("workFlowId") String workFlowId);
}
