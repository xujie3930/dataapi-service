package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditWorkFlowEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 业务流程 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/workFlow")
public class IcreditWorkFlowController {

    @Autowired
    private IcreditWorkFlowService workFlowService;

    @PostMapping("/hasExist")
    public BusinessResult<Boolean> hasExitTheme(@RequestBody WorkFlowSaveRequest request) {
        return BusinessResult.success(workFlowService.hasExit(request));
    }

    @PostMapping("/save")
    BusinessResult<String> saveDef(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody WorkFlowSaveRequest request) {
        return workFlowService.saveDef(userId, request);
    }

    @PostMapping("/list")
    BusinessResult<List<IcreditWorkFlowEntity>> list() {
        return workFlowService.getList();
    }

    @PostMapping("/rearrangement")
    BusinessResult<Boolean> rearrangement(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody Map<String, String[]> request) {
        return workFlowService.rearrangement(userId, request);
    }

}

