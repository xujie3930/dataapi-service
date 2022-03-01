package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.AppQueryListDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppGroupMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.AppQueryListParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppQueryListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppGroupSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.CheckAppGroupNameRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppGroupQueryListResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppQueryListResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    public BusinessResult<List<AppGroupQueryListResult>> getList(AppQueryListRequest request) {
        AppQueryListParam param = BeanCopyUtils.copyProperties(request, new AppQueryListParam());
        List<AppQueryListDTO> list = appGroupMapper.getList(param);
        List<AppGroupQueryListResult> appGroupQueryListResultList = new ArrayList<>();
        for (AppQueryListDTO appQueryListDTO : list) {
            if(StringUtils.isEmpty(appQueryListDTO.getAppGroupId())){//第一层级
                AppGroupQueryListResult appGroupQueryListResult = new AppGroupQueryListResult();
                BeanUtils.copyProperties(appQueryListDTO, appGroupQueryListResult);
                appGroupQueryListResultList.add(appGroupQueryListResult);
            }
        }
        for (AppGroupQueryListResult appGroupQueryListResult : appGroupQueryListResultList) {
            List<AppQueryListResult> appQueryListResultList = getChildren(appGroupQueryListResult.getId(), list);
            appGroupQueryListResult.setChildren(appQueryListResultList);
        }
        return BusinessResult.success(appGroupQueryListResultList);
    }

    private List<AppQueryListResult> getChildren(String id, List<AppQueryListDTO> list) {
        List<AppQueryListResult> appQueryListResultList = new ArrayList<>();
        for (AppQueryListDTO appQueryListDTO : list) {
            if(id.equals(appQueryListDTO.getAppGroupId())){
                AppQueryListResult appQueryListResult = new AppQueryListResult();
                BeanUtils.copyProperties(appQueryListDTO, appQueryListResult);
                appQueryListResultList.add(appQueryListResult);
            }
        }
        return appQueryListResultList;
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
        String generateId = RandomStringUtils.randomNumeric(STR_RAND_LENGTH);
        while (generateId.startsWith("0")){
            generateId = RandomStringUtils.randomNumeric(STR_RAND_LENGTH);
        }
        return BusinessResult.success(generateId);
    }
}
