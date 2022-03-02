package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiGroupMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiGroupIdAndNameListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiGroupListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiGroupSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGroupResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.vo.GroupIdAndNameVO;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.validate.BusinessParamsValidate;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Autowired
    private IcreditApiGroupMapper apiGroupMapper;

    @Override
    @BusinessParamsValidate(argsIndexs = {1})
    public BusinessResult<String> saveDef(String userId, ApiGroupSaveRequest request) {
        StringLegalUtils.checkLegalName(request.getName());
        checkRepetitionName(request.getName(), null);
        IcreditApiGroupEntity entity = getApiGroupEntitySaveEntity(request);
        save(entity);
        return BusinessResult.success(entity.getId());
    }

    @Override
    public Boolean hasExit(WorkFlowSaveRequest request) {
        return BooleanUtils.isTrue(apiGroupMapper.hasExit(request.getName(), null));
    }

    @Override
    @ResultReturning
    public BusinessResult<List<IcreditApiGroupEntity>> getList(ApiGroupListRequest request) {
        List<IcreditApiGroupEntity> list = list(queryWrapper(request));
        return BusinessResult.success(list);
    }

    @Override
    public List<ApiGroupResult> getByWorkId(String workFlowId) {
        return apiGroupMapper.getByWorkId(workFlowId);
    }

    @Override
    @ResultReturning
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
    public BusinessResult<List<GroupIdAndNameVO>> getGroupListByWorkFlowId(ApiGroupIdAndNameListRequest request) {
        return BusinessResult.success(apiGroupMapper.getGroupListByWorkFlowId(request.getWorkIds()));
    }
}
