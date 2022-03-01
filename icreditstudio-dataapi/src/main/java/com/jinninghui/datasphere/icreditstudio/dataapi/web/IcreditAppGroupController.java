package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAppGroupEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppGroupService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppGroupListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppGroupSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.CheckAppGroupNameRequest;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 应用分组 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/appGroup")
public class IcreditAppGroupController {

    @Autowired
    private IcreditAppGroupService appGroupService;

    @PostMapping("/save")
    BusinessResult<String> saveDef(@RequestHeader(value = "userId") String userId, @RequestBody AppGroupSaveRequest request) {
        return appGroupService.saveDef(userId, request);
    }

    @PostMapping("/list")
    BusinessResult<List<IcreditAppGroupEntity>> list(@RequestBody AppGroupListRequest request) {
        return appGroupService.getList(request);
    }

    @PostMapping("/generateId")
    BusinessResult<String> generateId() {
        return appGroupService.generateId();
    }

    @PostMapping("/checkAppGroupName")
    public BusinessResult<Boolean> checkAppGroupName(@RequestBody CheckAppGroupNameRequest request) {
        return appGroupService.checkAppGroupName(request);
    }
}