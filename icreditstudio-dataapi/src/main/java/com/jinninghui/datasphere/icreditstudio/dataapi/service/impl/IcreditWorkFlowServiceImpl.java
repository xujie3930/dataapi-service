package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.dto.ApiInfoDTO;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditWorkFlowEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditWorkFlowMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.StringLegalUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowDelRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowRenameRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiGroupResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowDelResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowIdAndNameResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import com.jinninghui.datasphere.icreditstudio.framework.validate.BusinessParamsValidate;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * ???????????? ???????????????
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Service
public class IcreditWorkFlowServiceImpl extends ServiceImpl<IcreditWorkFlowMapper, IcreditWorkFlowEntity> implements IcreditWorkFlowService {

    @Resource
    private IcreditWorkFlowMapper workFlowMapper;
    @Resource
    private IcreditApiGroupService apiGroupService;
    @Resource
    private IcreditApiBaseService apiService;
    private static String DEFAULT_WORK_FLOW_ID = "0";

    @Override
    public Boolean hasExit(WorkFlowSaveRequest request) {
        return BooleanUtils.isTrue(workFlowMapper.hasExit(request.getName(), null));
    }

    @Override
    @BusinessParamsValidate(argsIndexs = {1})
    public BusinessResult<String> saveDef(String userId, WorkFlowSaveRequest request) {
        StringLegalUtils.checkLegalName(request.getName());
        checkRepetitionName(request.getName(), request.getId());
        IcreditWorkFlowEntity entity = null;
        if(StringUtils.isEmpty(request.getId())){
            entity = getWorkFlowEntitySaveEntity(request);
        }else{
            IcreditWorkFlowEntity oldWorkFlowEntity = workFlowMapper.selectById(request.getId());
            entity = new IcreditWorkFlowEntity();
            BeanUtils.copyProperties(request, entity);
            entity.setSort(oldWorkFlowEntity.getSort());
        }
        saveOrUpdate(entity);
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
            //?????????????????????????????????
            IcreditWorkFlowEntity workFlowEntity = getById(workFlowId);
            //??????????????????????????????,????????????
            if (Objects.isNull(workFlowEntity)) {
                throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000004.getCode());
            }
            workFlowMapper.updateSort(size, workFlowId);
            size -= 1;
            if (apiGroupIds.length <= 0) {
                continue;
            }

            //????????????????????????API????????????????????????
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
        //??????????????????????????????
        List<WorkFlowResult> workFlowResults = workFlowMapper.searchFromName(request.getName());
        Set<String> workFlowIds = new HashSet<>();
        for (WorkFlowResult workFlowResult : workFlowResults) {
            String workFlowId = workFlowResult.getWorkFlowId();
            List<ApiGroupResult> apiGroupResults = apiGroupService.getByWorkId(workFlowId);
            workFlowResult.setApiGroup(apiGroupResults);
            workFlowIds.add(workFlowId);
        }

        //??????API??????????????????
        List<IcreditApiGroupEntity> apiGroupEntityList = apiGroupService.searchFromName(request.getName());
        for (IcreditApiGroupEntity apiGroupEntity : apiGroupEntityList) {
            if (workFlowIds.contains(apiGroupEntity.getWorkId())) {
                continue;
            }
            //??????????????????????????????????????????????????????????????????
            WorkFlowResult workFlowResult;
            Boolean toAdd = false;
            Optional<WorkFlowResult> first = workFlowResults.stream()
                    .filter(p -> apiGroupEntity.getWorkId().equals(p.getWorkFlowId())).findFirst();
            if (first.isPresent()){
                workFlowResult = first.get();
            }else {
                IcreditWorkFlowEntity workFlowEntity = getById(apiGroupEntity.getWorkId());
                workFlowResult = new WorkFlowResult(workFlowEntity.getName(), workFlowEntity.getId(), new LinkedList<>());
                toAdd = true;
            }
            IcreditApiGroupEntity tempApiGroupEntity = apiGroupService.getById(apiGroupEntity.getId());
            ApiGroupResult apiGroupResult = new ApiGroupResult(tempApiGroupEntity.getName(), tempApiGroupEntity.getId());
            workFlowResult.getApiGroup().add(apiGroupResult);
            //??????????????????
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
     * save????????????id???null???update??????????????????id
     *
     * @param name
     * @param id
     */
    private void checkRepetitionName(String name, String id) {
        boolean hasExit = BooleanUtils.isTrue(workFlowMapper.hasExit(name, id));
        if (hasExit) {
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_10000002.getCode());
        }
    }

    @Override
    public BusinessResult<List<WorkFlowIdAndNameResult>> getWorkFlowList() {
        return BusinessResult.success(workFlowMapper.getWorkFlowList());
    }

    @Override
    public List<ApiInfoDTO> findApiInfoByApiIds(List<String> apiIds, Integer publishStatus) {
        Map<String, Object> paramsMap = new HashMap<>(4);
        paramsMap.put("list", apiIds);
        paramsMap.put("publishStatus", publishStatus);
        return workFlowMapper.findApiInfoByApiIds(paramsMap);
    }

    @Override
    @Transactional
    public BusinessResult<WorkFlowDelResult> delById(WorkFlowDelRequest request) {
        StringLegalUtils.checkId(request.getId());
        if(DEFAULT_WORK_FLOW_ID.equals(request.getId())){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000039.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000039.getMessage());
        }
        String apiId = apiService.findPublishedByWorkFlowId(request.getId());
        if(StringUtils.isNotEmpty(apiId)){
            throw new AppException(ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000037.getCode(), ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000037.getMessage());
        }

        IcreditWorkFlowEntity workFlowEntity = workFlowMapper.selectById(request.getId());

        List<String> apiIdList = null;
        List<String> apiGroupIdList = apiGroupService.getIdsByWorkId(request.getId());
        if(!CollectionUtils.isEmpty(apiGroupIdList)) {
            apiIdList = apiService.getIdsByApiGroupIds(apiGroupIdList);
            apiGroupService.removeByIds(apiGroupIdList);
        }

        if(!CollectionUtils.isEmpty(apiIdList)) {
            apiService.removeByIds(apiIdList);
        }
        workFlowMapper.deleteById(request.getId());

        String nextSelectedWorkId = workFlowMapper.findNextWorkId(workFlowEntity.getSort());
        if(StringUtils.isEmpty(nextSelectedWorkId)){//???????????????api??????
            nextSelectedWorkId = workFlowMapper.getFirstWorkFlowId();
        }
        WorkFlowDelResult workFlowDelResult = new WorkFlowDelResult();
        workFlowDelResult.setWorkId(nextSelectedWorkId);
        workFlowDelResult.setApiGroupId(apiGroupService.getFirstApiGroupForWorkFlow(workFlowDelResult.getWorkId()));
        return BusinessResult.success(workFlowDelResult);
    }

    @Override
    public BusinessResult<Boolean> renameById(WorkFlowRenameRequest request) {
        StringLegalUtils.checkId(request.getId());
        StringLegalUtils.checkLegalName(request.getNewName());
        checkRepetitionName(request.getNewName(), request.getId());
        workFlowMapper.renameById(request.getNewName(), request.getId());
        return BusinessResult.success(true);
    }
}
