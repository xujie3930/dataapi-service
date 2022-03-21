package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.AppQueryListDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.AppQueryListParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 应用分组 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAppGroupMapper extends BaseMapper<IcreditAppGroupEntity> {

    List<AppQueryListDTO> getList(@RequestBody AppQueryListParam param);

    Boolean hasExitByName(@Param("name") String name, @Param("id") String id);

    Boolean hasExitByGenerateId(@Param("generateId") String generateId, @Param("id") String id);

    String findNameById(@Param("id") String id);

    void renameById(@Param("id") String id, @Param("name") String name, @Param("desc") String desc);
}
