package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiParamEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiParamMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * api参数 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditApiParamServiceImpl extends ServiceImpl<IcreditApiParamMapper, IcreditApiParamEntity> implements IcreditApiParamService {

    @Autowired
    private IcreditApiParamMapper apiParamMapper;

    @Override
    @ResultReturning
    public List<IcreditApiParamEntity> getByApiBaseId(String id) {
        return apiParamMapper.getByApiBaseId(id);
    }

    @Override
    public List<IcreditApiParamEntity> getByApiIdAndVersion(String id, Integer apiVersion) {
        return apiParamMapper.getByApiIdAndVersion(id, apiVersion);
    }
}
