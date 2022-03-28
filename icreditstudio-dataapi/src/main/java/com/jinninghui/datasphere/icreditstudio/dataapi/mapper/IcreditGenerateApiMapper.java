package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditGenerateApiEntity;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 生成API表 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditGenerateApiMapper extends BaseMapper<IcreditGenerateApiEntity> {

    IcreditGenerateApiEntity getByApiIdAndVersion(@Param("id") String id, @Param("apiVersion") Integer apiVersion);

    void removeByApiId(@Param("apiId") String apiId, @Param("apiVersion") Integer apiVersion);

    void deleteByApiIdAndVersion(@Param("apiId") String apiId, @Param("apiVersion") Integer apiVersion);
}
