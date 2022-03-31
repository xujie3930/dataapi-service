package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.dto.RegisterApiDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditRegisterApiEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditRegisterApiMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditRegisterApiService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 注册API 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditRegisterApiServiceImpl extends ServiceImpl<IcreditRegisterApiMapper, IcreditRegisterApiEntity> implements IcreditRegisterApiService {

    @Resource
    private IcreditRegisterApiMapper registerApiMapper;

    @Override
    public IcreditRegisterApiEntity findByApiIdAndApiVersion(String apiBaseId, Integer apiVersion) {
        return registerApiMapper.findByApiIdAndApiVersion(apiBaseId, apiVersion);
    }

    @Override
    public void deleteByApiIdAndApiVersion(String apiBaseId, Integer apiVersion) {
        registerApiMapper.deleteByApiIdAndApiVersion(apiBaseId, apiVersion);
    }

    @Override
    public LinkedList<RegisterApiDTO> findWaitPublishedByApiId(String apiBaseId) {
        return registerApiMapper.findWaitPublishedByApiId(apiBaseId);
    }
}
