package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditRegisterApiEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 注册API Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditRegisterApiMapper extends BaseMapper<IcreditRegisterApiEntity> {

    IcreditRegisterApiEntity findByApiIdAndApiVersion(@Param("apiBaseId") String apiBaseId, @Param("apiVersion") Integer apiVersion);

    void deleteByApiIdAndApiVersion(@Param("apiId") String apiId, @Param("apiVersion") Integer apiVersion);
}
