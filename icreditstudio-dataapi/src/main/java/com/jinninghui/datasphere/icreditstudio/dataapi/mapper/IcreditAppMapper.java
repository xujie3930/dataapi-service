package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.AppEnableRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 应用 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAppMapper extends BaseMapper<IcreditAppEntity> {

    Boolean hasExistappFlag(@Param("appFlag") String appFlag);

    List<IcreditAppEntity> getList(@Param("appGroupId") String appGroupId);

    Boolean enableById(@Param("request") AppEnableRequest request);
}
