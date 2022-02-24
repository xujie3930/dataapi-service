package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditGenerateApiEntity;

/**
 * <p>
 * 生成API表 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditGenerateApiService extends IService<IcreditGenerateApiEntity> {

    IcreditGenerateApiEntity getByApiBaseId(String id);
}
