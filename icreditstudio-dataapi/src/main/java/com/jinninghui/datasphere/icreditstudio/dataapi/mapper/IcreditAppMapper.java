package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppEnableRequest;
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

    Boolean enableById(@Param("request") AppEnableRequest request);

    IcreditAppEntity getByAppFlag(@Param("appFlag") String appFlag);

    Boolean hasExitByGenerateId(@Param("generateId") String generateId, @Param("appGroupId") String appGroupId);

    String findEnableAppIdByAppGroupIds(List<String> appGroupIds);

    List<String> getIdsByAppGroupIds(List<String> ids);

    String findEnableAppIdByIds(List<String> appIds);

    Long getCountByEnableAndDelFlag(@Param("isEnable") Integer isEnable, @Param("delFlag") Integer delFlag);
}
