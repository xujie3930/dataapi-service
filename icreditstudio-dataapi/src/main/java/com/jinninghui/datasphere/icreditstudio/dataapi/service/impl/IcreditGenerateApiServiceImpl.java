package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.DatasourceApiDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditGenerateApiEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditGenerateApiMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditGenerateApiService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;

/**
 * <p>
 * 生成API表 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditGenerateApiServiceImpl extends ServiceImpl<IcreditGenerateApiMapper, IcreditGenerateApiEntity> implements IcreditGenerateApiService {

    @Resource
    private IcreditGenerateApiMapper generateApiMapper;

    @Override
    public IcreditGenerateApiEntity getByApiIdAndVersion(String id, Integer apiVersion) {
        return generateApiMapper.getByApiIdAndVersion(id, apiVersion);
    }

    @Override
    public void removeByApiIdAndApiVersion(String id, Integer apiVersion) {
        generateApiMapper.removeByApiId(id, apiVersion);
    }

    @Override
    public void deleteByApiIdAndVersion(String id, Integer apiVersion) {
        generateApiMapper.deleteByApiIdAndVersion(id, apiVersion);
    }

    @Override
    public LinkedList<DatasourceApiDTO> findWaitPublishedByApiId(String apiBaseId) {
        return generateApiMapper.findWaitPublishedByApiId(apiBaseId);
    }
}
