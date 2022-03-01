package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppGroupMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.CharacterUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.AppGroupListParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppGroupListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppGroupSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.CheckAppGroupNameRequest;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Resource
    private IcreditAppGroupMapper appGroupMapper;
    @Resource
    private IcreditAppService appService;

    @Override
    public BusinessResult<String> saveDef(String userId, AppGroupSaveRequest request) {
        checkAppGroupName(new CheckAppGroupNameRequest(request.getId(), request.getName()));
        if(BooleanUtils.isTrue(appGroupMapper.hasExitByGenerateId(request.getGenerateId(), request.getId()))
                || BooleanUtils.isTrue(appService.hasExitByGenerateId(request.getGenerateId(), request.getId()))){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000013.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000013.getMessage());
        }
        IcreditAppGroupEntity appGroupEntity = BeanCopyUtils.copyProperties(request, new IcreditAppGroupEntity());
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
    public BusinessResult<Boolean> checkAppGroupName(CheckAppGroupNameRequest request) {
        if(!request.getName().matches("^[a-zA-Z|\u4e00-\u9fa5][a-zA-Z0-9\u4e00-\u9fa5]{1,50}$")){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000011.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000011.getMessage());
        }
        if(BooleanUtils.isTrue(appGroupMapper.hasExitByName(request.getName(), request.getId()))){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000012.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000012.getMessage());
        }
        return BusinessResult.success(true);
    }

    @Override
    public BusinessResult<String> generateId() {
        String generateId = RandomStringUtils.randomNumeric(11);
        while (generateId.startsWith("0")){
            generateId = RandomStringUtils.randomNumeric(11);
        }
        return BusinessResult.success(generateId);
    }
}
