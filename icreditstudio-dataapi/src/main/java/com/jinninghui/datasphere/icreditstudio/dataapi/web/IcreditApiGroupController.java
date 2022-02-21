package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditWorkFlowEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiGroupListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiGroupSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * API分组 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/apiGroup")
public class IcreditApiGroupController {

    @Autowired
    private IcreditApiGroupService apiGroupService;

    @PostMapping("/hasExist")
    public BusinessResult<Boolean> hasExitTheme(@RequestBody WorkFlowSaveRequest request) {
        return BusinessResult.success(apiGroupService.hasExit(request));
    }

    @PostMapping("/save")
    BusinessResult<String> saveDef(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody ApiGroupSaveRequest request) {
        return apiGroupService.saveDef(userId, request);
    }

    @PostMapping("/list")
    BusinessResult<List<IcreditApiGroupEntity>> list(@RequestBody ApiGroupListRequest request) {
        return apiGroupService.getList(request);
    }

}

