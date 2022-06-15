package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.AppQueryListDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppGroupMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.AppQueryListParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppGroupQueryListResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppQueryListResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BusinessResult<String> saveDef(String userId, AppGroupSaveRequest request) {
        BusinessResult<Boolean> checkResult = checkAppGroupName(new CheckAppGroupNameRequest(request.getId(), request.getName()));
        if(checkResult.getData()){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000047.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000047.getMessage());
        }
        if(BooleanUtils.isTrue(appGroupMapper.hasExitByGenerateId(request.getGenerateId(), request.getId()))
                || BooleanUtils.isTrue(appService.hasExitByGenerateId(request.getGenerateId(), request.getId()))){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000013.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000013.getMessage());
        }
        IcreditAppGroupEntity appGroupEntity = BeanCopyUtils.copyProperties(request, new IcreditAppGroupEntity());
        save(appGroupEntity);
        return BusinessResult.success(appGroupEntity.getId());
    }

    //查询所有的应用分组
    @Override
    @ResultReturning
    public BusinessResult<List<AppGroupQueryListResult>> getList(AppQueryListRequest request) {
        /*try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        AppQueryListParam param = BeanCopyUtils.copyProperties(request, new AppQueryListParam());
        List<AppQueryListDTO> list = appGroupMapper.getList(param);
        List<AppGroupQueryListResult> appGroupQueryListResultList = new ArrayList<>();
        for (AppQueryListDTO appQueryListDTO : list) {
            if(StringUtils.isEmpty(appQueryListDTO.getAppGroupId())){//第一层级
                AppGroupQueryListResult appGroupQueryListResult = new AppGroupQueryListResult();
                BeanUtils.copyProperties(appQueryListDTO, appGroupQueryListResult);
                appGroupQueryListResult.setCreateTime(appQueryListDTO.getCreateTime().getTime());
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
                appQueryListResult.setCreateTime(appQueryListDTO.getCreateTime().getTime());
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
        return BusinessResult.success(BooleanUtils.isTrue(appGroupMapper.hasExitByName(request.getName(), request.getId())));
    }

    @Override
    public BusinessResult<String> generateId() {
        String generateId = RandomStringUtils.randomNumeric(STR_RAND_LENGTH);
        while (generateId.startsWith("0")){
            generateId = RandomStringUtils.randomNumeric(STR_RAND_LENGTH);
        }
        return BusinessResult.success(generateId);
    }

    @Override
    public String findNameById(String appGroupId) {
        return appGroupMapper.findNameById(appGroupId);
    }

    @Override
    public BusinessResult<Boolean> renameById(AppGroupRenameRequest request) {
        StringLegalUtils.checkId(request.getId());
        BusinessResult<Boolean> checkResult = checkAppGroupName(new CheckAppGroupNameRequest(request.getId(), request.getName()));
        if(checkResult.getData()){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000047.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000047.getMessage());
        }
        appGroupMapper.renameById(request.getId(), request.getName(), request.getDesc());
        return BusinessResult.success(true);
    }

    @Override
    @Transactional
    public BusinessResult<Boolean> delByIds(AppGroupDelRequest request) {
        if(CollectionUtils.isEmpty(request.getAppGroupIds()) && CollectionUtils.isEmpty(request.getAppIds())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000048.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000048.getMessage());
        }
        List<String> appIdList = new ArrayList<>();
        String appId = null;
        if(!CollectionUtils.isEmpty(request.getAppIds())){
            appId = appService.findEnableAppIdByIds(request.getAppIds());
            appIdList.addAll(request.getAppIds());
        }
        if(StringUtils.isNotEmpty(appId)){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000049.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000049.getMessage());
        }
        if(!CollectionUtils.isEmpty(request.getAppGroupIds())){
            appId = appService.findEnableAppIdByAppGroupIds(request.getAppGroupIds());
            List<String> appIds = appService.getIdsByAppGroupIds(request.getAppGroupIds());
            appIdList.addAll(appIds);
        }
        if(StringUtils.isNotEmpty(appId)){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000049.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000049.getMessage());
        }
        if(!CollectionUtils.isEmpty(request.getAppGroupIds())){
            removeByIds(request.getAppGroupIds());
        }
        if(!CollectionUtils.isEmpty(appIdList)) {
            appService.removeByIds(appIdList);
        }
        return BusinessResult.success(true);
    }
}
