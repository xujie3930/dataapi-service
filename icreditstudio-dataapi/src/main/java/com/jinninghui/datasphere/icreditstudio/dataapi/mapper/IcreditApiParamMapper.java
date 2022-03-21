package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiParamEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * api参数 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiParamMapper extends BaseMapper<IcreditApiParamEntity> {

    List<IcreditApiParamEntity> getByApiIdAndVersion(@Param("apiId") String apiId, @Param("apiVersion") Integer apiVersion);

    void removeByApiId(@Param("apiId") String apiId);
}
