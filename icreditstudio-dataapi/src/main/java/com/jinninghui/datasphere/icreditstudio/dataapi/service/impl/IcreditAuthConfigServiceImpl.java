package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ICreditAuthConfigUpdateDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthConfigEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthConfigMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 授权配置 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditAuthConfigServiceImpl extends ServiceImpl<IcreditAuthConfigMapper, IcreditAuthConfigEntity> implements IcreditAuthConfigService {

    @Autowired
    private IcreditAuthConfigMapper configMapper;
    @Override
    @Transactional
    public int deleteByIds(Collection<String> ids) {
        final Map<String, Object> deleteMap = new HashMap<>(2);
        deleteMap.put("ids", ids);
        return configMapper.deletes(deleteMap);
    }

    @Override
    public int updateByIds(IcreditAuthConfigEntity entity, Collection<String> ids) {
        if(null==ids || ids.isEmpty() || null==entity){
            return 0;
        }
        ICreditAuthConfigUpdateDTO dto = new ICreditAuthConfigUpdateDTO();
        dto.setIds(ids);
        dto.setAllowCall(entity.getAllowCall());
        dto.setPeriodBegin(entity.getPeriodBegin());
        dto.setPeriodEnd(entity.getPeriodEnd());
        return configMapper.updateByIds(dto);
    }
}
