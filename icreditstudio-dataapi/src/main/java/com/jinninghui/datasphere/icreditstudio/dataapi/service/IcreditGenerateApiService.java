package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.DatasourceApiDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditGenerateApiEntity;

import java.util.LinkedList;

/**
 * <p>
 * 生成API表 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditGenerateApiService extends IService<IcreditGenerateApiEntity> {

    IcreditGenerateApiEntity getByApiIdAndVersion(String id, Integer apiVersion);

    void removeByApiIdAndApiVersion(String id, Integer apiVersion);

    void deleteByApiIdAndVersion(String id, Integer apiVersion);

    LinkedList<DatasourceApiDTO> findWaitPublishedByApiId(String apiBaseId);
}
