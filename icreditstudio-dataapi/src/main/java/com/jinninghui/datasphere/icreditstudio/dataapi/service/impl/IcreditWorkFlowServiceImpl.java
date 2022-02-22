package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditWorkFlowEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditWorkFlowMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGroupResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.validate.BusinessParamsValidate;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务流程 服务实现类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditWorkFlowServiceImpl extends ServiceImpl<IcreditWorkFlowMapper, IcreditWorkFlowEntity> implements IcreditWorkFlowService {

    @Autowired
    private IcreditWorkFlowMapper workFlowMapper;
    @Autowired
    private IcreditApiGroupService apiGroupService;

    @Override
    public Boolean hasExit(WorkFlowSaveRequest request) {
        return BooleanUtils.isTrue(workFlowMapper.hasExit(request.getName(), null));
    }

    @Override
    @BusinessParamsValidate(argsIndexs = {1})
    public BusinessResult<String> saveDef(String userId, WorkFlowSaveRequest request) {
        checkRepetitionName(request.getName(), null);
        IcreditWorkFlowEntity entity = getWorkFlowEntitySaveEntity(request);
        save(entity);
        return BusinessResult.success(entity.getId());
    }

    @Override
    public BusinessResult<List<IcreditWorkFlowEntity>> getList() {
        List<IcreditWorkFlowEntity> list = list(queryWrapper());
        return BusinessResult.success(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessResult<Boolean> rearrangement(String userId, Map<String, String[]> request) {
        if (CollectionUtils.isEmpty(request)) {
            return BusinessResult.success(true);
        }
        int size = request.entrySet().size();
        for (Map.Entry<String, String[]> m : request.entrySet()) {

            String workFlowId = m.getKey();
            String[] apiGroupIds = m.getValue();
            //调换不同主题之间的排序
            IcreditWorkFlowEntity workFlowEntity = getById(workFlowId);
            //二级目录拖到一级目录,需要报错
            if (Objects.isNull(workFlowEntity)) {
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000004.getCode());
            }
            workFlowMapper.updateSort(size, workFlowId);
            size -= 1;
            if (apiGroupIds.length <= 0) {
                continue;
            }

            //更新排序，并更新API分组所属业务流程
            int resourceSize = apiGroupIds.length;
            for (String apiGroupId : apiGroupIds) {
                IcreditApiGroupEntity apiGroupEntity = apiGroupService.getById(apiGroupId);
                if (Objects.isNull(apiGroupEntity)) {
                    throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000005.getCode());
                }
                apiGroupEntity.setSort(resourceSize);
                apiGroupEntity.setWorkId(workFlowId);
                apiGroupService.updateById(apiGroupEntity);
                resourceSize -= 1;
            }
        }
        return BusinessResult.success(true);
    }

    @Override
    public List<WorkFlowResult> searchFromName(WorkFlowSaveRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            return new LinkedList<>();
        }
        //根据业务流程名称搜索
        List<WorkFlowResult> workFlowResults = workFlowMapper.searchFromName(request.getName());
        Set<String> workFlowIds = new HashSet<>();
        for (WorkFlowResult workFlowResult : workFlowResults) {
            String workFlowId = workFlowResult.getWorkFlowId();
            List<ApiGroupResult> apiGroupResults = apiGroupService.getByWorkId(workFlowId);
            workFlowResult.setApiGroup(apiGroupResults);
            workFlowIds.add(workFlowId);
        }

        //根据API分组名称搜索
        List<IcreditApiGroupEntity> apiGroupEntityList = apiGroupService.searchFromName(request.getName());
        for (IcreditApiGroupEntity apiGroupEntity : apiGroupEntityList) {
            if (workFlowIds.contains(apiGroupEntity.getWorkId())) {
                continue;
            }
            IcreditWorkFlowEntity workFlowEntity = getById(apiGroupEntity.getWorkId());
            WorkFlowResult workFlowResult;
            Boolean toAdd = false;
            Optional<WorkFlowResult> first = workFlowResults.stream()
                    .filter(p -> {
                        return apiGroupEntity.getWorkId().equals(p.getWorkFlowId());
                    }).findFirst();
            if (first.isPresent()){
                workFlowResult = first.get();
            }else {
                workFlowResult = new WorkFlowResult(workFlowEntity.getName(), workFlowEntity.getId(), new LinkedList<>());
                toAdd = true;
            }
            IcreditApiGroupEntity tempApiGroupEntity = apiGroupService.getById(apiGroupEntity.getId());
            ApiGroupResult apiGroupResult = new ApiGroupResult(tempApiGroupEntity.getName(), tempApiGroupEntity.getId());
            workFlowResult.getApiGroup().add(apiGroupResult);
            //避免重复添加
            if (toAdd){
                workFlowResults.add(workFlowResult);
            }
        }
        return workFlowResults;
    }

    private Wrapper<IcreditWorkFlowEntity> queryWrapper() {
        QueryWrapper<IcreditWorkFlowEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(IcreditWorkFlowEntity.DEL_FLAG, DelFlagEnum.ENA_BLED.getCode());
        wrapper.orderByDesc(IcreditWorkFlowEntity.SORT);
        return wrapper;
    }

    private IcreditWorkFlowEntity getWorkFlowEntitySaveEntity(WorkFlowSaveRequest request) {
        IcreditWorkFlowEntity entity = BeanCopyUtils.copyProperties(request, new IcreditWorkFlowEntity());
        Integer maxSort = workFlowMapper.getMaxSort();
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
        boolean hasExit = BooleanUtils.isTrue(workFlowMapper.hasExit(name, id));
        if (hasExit) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000003.getCode());
        }
    }
}
