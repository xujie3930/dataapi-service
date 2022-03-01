package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppGroupMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.CharacterUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.AppGroupListParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppGroupListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppGroupSaveRequest;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 应用分组 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditAppGroupServiceImpl extends ServiceImpl<IcreditAppGroupMapper, IcreditAppGroupEntity> implements IcreditAppGroupService {

    private static final Integer STR_RAND_LENGTH = 11;
    @Autowired
    private IcreditAppGroupMapper appGroupMapper;

    @Override
    public BusinessResult<String> saveDef(String userId, AppGroupSaveRequest request) {
        String appGroupId = CharacterUtils.getRandomString(STR_RAND_LENGTH);
        while (BooleanUtils.isTrue(appGroupMapper.hasExistAppGroupId(appGroupId))) {
            appGroupId = CharacterUtils.getRandomString(STR_RAND_LENGTH);
        }
        IcreditAppGroupEntity appGroupEntity = BeanCopyUtils.copyProperties(request, new IcreditAppGroupEntity());
        appGroupEntity.setAppGroupId(appGroupId);
        save(appGroupEntity);
        return BusinessResult.success(appGroupEntity.getId());
    }

    @Override
    @ResultReturning
    //查询所有的应用分组
    public BusinessResult<List<IcreditAppGroupEntity>> getList(AppGroupListRequest request) {
        AppGroupListParam param = BeanCopyUtils.copyProperties(request, new AppGroupListParam());
        List<IcreditAppGroupEntity> list = appGroupMapper.getList(param);
        return BusinessResult.success(list);
    }

    @Override
    public Boolean hasExit(AppGroupSaveRequest request) {
        return BooleanUtils.isTrue(appGroupMapper.hasExit(request.getName(), null));
    }

}
