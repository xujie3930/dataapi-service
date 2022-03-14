package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiGroupMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGroupResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.GroupIdAndNameResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.validate.BusinessParamsValidate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * API分组 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditApiGroupServiceImpl extends ServiceImpl<IcreditApiGroupMapper, IcreditApiGroupEntity> implements IcreditApiGroupService {

    @Resource
    private IcreditApiGroupMapper apiGroupMapper;
    @Resource
    private IcreditApiBaseService apiBaseService;
    private static String DEFAULT_API_GROUP_ID = "000";

    @Override
    @BusinessParamsValidate(argsIndexs = {1})
    public BusinessResult<Map<String, String>> saveDef(String userId, ApiGroupSaveRequest request) {
        StringLegalUtils.checkLegalName(request.getName());
        checkRepetitionName(request.getName(), null);
        IcreditApiGroupEntity entity = getApiGroupEntitySaveEntity(request);
        save(entity);
        Map<String, String> map = new HashMap<>();
        map.put("workId", request.getWorkId());
        map.put("apiGroupId", entity.getId());
        return BusinessResult.success(map);
    }

    @Override
    public Boolean hasExit(WorkFlowSaveRequest request) {
        return BooleanUtils.isTrue(apiGroupMapper.hasExit(request.getName(), null));
    }

    @Override
    public BusinessResult<List<IcreditApiGroupEntity>> getList(ApiGroupListRequest request) {
        List<IcreditApiGroupEntity> list = list(queryWrapper(request));
        return BusinessResult.success(list);
    }

    @Override
    public List<ApiGroupResult> getByWorkId(String workFlowId) {
        return apiGroupMapper.getByWorkId(workFlowId);
    }

    @Override
    public List<IcreditApiGroupEntity> searchFromName(String name) {
        return apiGroupMapper.searchFromName(name);
    }

    private Wrapper<IcreditApiGroupEntity> queryWrapper(ApiGroupListRequest request) {
        QueryWrapper<IcreditApiGroupEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(IcreditApiGroupEntity.DEL_FLAG, DelFlagEnum.ENA_BLED.getCode());
        wrapper.eq(IcreditApiGroupEntity.WORK_ID, request.getWorkId());
        wrapper.orderByDesc(IcreditApiGroupEntity.SORT);
        return wrapper;
    }

    private IcreditApiGroupEntity getApiGroupEntitySaveEntity(ApiGroupSaveRequest request) {
        IcreditApiGroupEntity entity = BeanCopyUtils.copyProperties(request, new IcreditApiGroupEntity());
        Integer maxSort = apiGroupMapper.getMaxSort(request.getWorkId());
        entity.setSort(maxSort + 1);
        return entity;
    }

    /**
     * save的情况下id传null，update时候则传主键id
     *
     * @param name
     * @param id
     */
    private void checkRepetitionName(String name, String id) {
        boolean hasExit = BooleanUtils.isTrue(apiGroupMapper.hasExit(name, id));
        if (hasExit) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000003.getCode());
        }
    }

    @Override
    public BusinessResult<List<GroupIdAndNameResult>> getGroupListByWorkFlowId(ApiGroupIdAndNameListRequest request) {
        return BusinessResult.success(apiGroupMapper.getGroupListByWorkFlowId(request.getWorkIds()));
    }

    @Override
    public BusinessResult<Boolean> renameById(ApiGroupRenameRequest request) {
        StringLegalUtils.checkId(request.getId());
        StringLegalUtils.checkLegalName(request.getNewName());
        checkRepetitionName(request.getNewName(), request.getId());
        apiGroupMapper.renameById(request.getNewName(), request.getId());
        return BusinessResult.success(true);
    }

    @Override
    public List<String> getIdsByWorkId(String workFlowId){
        return apiGroupMapper.getIdsByWorkId(workFlowId);
    }

    @Override
    @Transactional
    public BusinessResult<Boolean> delById(ApiGroupDelRequest request) {
        StringLegalUtils.checkId(request.getId());
        if(DEFAULT_API_GROUP_ID.equals(request.getId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000040.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000040.getMessage());
        }
        String apiId = apiBaseService.findPublishedByApiGroupId(request.getId());
        if(StringUtils.isNotEmpty(apiId)){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000038.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000038.getMessage());
        }
        List<String> apiGroupIdList = new ArrayList<>();
        apiGroupIdList.add(request.getId());
        List<String> apiIdList = apiBaseService.getIdsByApiGroupIds(apiGroupIdList);
        if(!CollectionUtils.isEmpty(apiIdList)) {
            apiBaseService.removeByIds(apiIdList);
        }
        apiGroupMapper.deleteById(request.getId());
        return BusinessResult.success(true);
    }
}
