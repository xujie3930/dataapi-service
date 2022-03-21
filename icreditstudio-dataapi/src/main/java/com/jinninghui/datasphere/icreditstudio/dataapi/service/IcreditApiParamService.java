package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiParamEntity;

import java.util.List;

/**
 * <p>
 * api参数 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiParamService extends IService<IcreditApiParamEntity> {

    List<IcreditApiParamEntity> getByApiIdAndVersion(String id, Integer apiVersion);

    void removeByApiId(String id);
}
