package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 授权表 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAuthMapper extends BaseMapper<IcreditAuthEntity> {

    List<IcreditAuthEntity> findByAppId(@Param("appId") String appId);

    void removeByAppId(@Param("appId") String appId);
}
