package com.jinninghui.datasphere.icreditstudio.dataapi.mapper;

import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ICreditAuthConfigUpdateDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 授权配置 Mapper 接口
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditAuthConfigMapper extends BaseMapper<IcreditAuthConfigEntity> {

    int deletes(final Map<String, Object> paramsMap);

    int updateByIds(ICreditAuthConfigUpdateDTO entity);
}
