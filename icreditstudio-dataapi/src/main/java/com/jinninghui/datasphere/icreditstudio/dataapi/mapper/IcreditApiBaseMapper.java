package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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

    void updatePublishStatusById(@Param("id") String id, @Param("publishStatus") Integer publishStatus);
}
