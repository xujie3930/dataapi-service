package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.dto.RegisterApiDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditRegisterApiEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.TableNameListRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 注册API 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditRegisterApiService extends IService<IcreditRegisterApiEntity> {

    IcreditRegisterApiEntity findByApiIdAndApiVersion(String apiBaseId, Integer apiVersion);

    void deleteByApiIdAndApiVersion(String apiBaseId, Integer apiVersion);

    LinkedList<RegisterApiDTO> findWaitPublishedByApiId(String apiBaseId);
}
