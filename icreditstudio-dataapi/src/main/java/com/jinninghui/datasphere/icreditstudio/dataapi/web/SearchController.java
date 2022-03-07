package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.CharacterUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 注册API 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/")
public class SearchController {

    @Autowired
    private IcreditWorkFlowService icreditWorkFlowService;

    @PostMapping("/search")
    public BusinessResult<List<WorkFlowResult>> searchFromName(@RequestBody WorkFlowSaveRequest request) {
        List<WorkFlowResult> workFlowResults = icreditWorkFlowService.searchFromName(request);
        return BusinessResult.success(workFlowResults);
    }

    @GetMapping ("/generate/random")
    public BusinessResult<String> generateRandom(@RequestParam(value = "len", defaultValue = "16") Integer len) {
        String randomString = CharacterUtils.getRandomString(len);
        return BusinessResult.success(randomString);
    }
}

