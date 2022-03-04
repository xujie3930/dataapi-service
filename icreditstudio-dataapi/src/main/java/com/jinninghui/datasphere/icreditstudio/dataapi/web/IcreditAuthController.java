package com.jinninghui.datasphere.icreditstudio.dataapi.web;

import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.AuthSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthInfoResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 授权表 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/auth")
public class IcreditAuthController {

    @Autowired
    private IcreditAuthService authService;

    @PostMapping("/save")
    BusinessResult<Boolean> saveDef(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody AuthSaveRequest request) {
        return authService.saveDef(userId, request);
    }

    @PostMapping("/info")
    BusinessResult<AuthInfoResult> authInfo(@RequestBody AuthInfoRequest request) {
        return authService.authInfo(request);
    }

}

