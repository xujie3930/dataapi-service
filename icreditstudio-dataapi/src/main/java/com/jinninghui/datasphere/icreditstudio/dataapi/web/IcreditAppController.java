package com.jinninghui.datasphere.icreditstudio.dataapi.web;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAppService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppDetailRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppEnableRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AppSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AppUpdatePageInfoResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 应用 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/app")
public class IcreditAppController {

    @Autowired
    private IcreditAppService appService;

    @PostMapping("/save")
    BusinessResult<String> saveDef(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody AppSaveRequest request) {
        return appService.saveDef(userId, request);
    }

    @PostMapping("/enableOrStop")
    BusinessResult<Boolean> enableOrStop(@RequestBody AppEnableRequest request) {
        return appService.enableOrStop(request);
    }

    @PostMapping("/detail")
    @ResultReturning
    BusinessResult<AppDetailResult> detail(@RequestBody AppDetailRequest request) {
        return appService.detail(request);
    }

    @PostMapping("/updatePageInfo")
    @ResultReturning
    BusinessResult<AppUpdatePageInfoResult> updatePageInfo(@RequestBody AppDetailRequest request) {
        return appService.updatePageInfo(request);
    }

}

