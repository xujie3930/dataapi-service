package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.web;


import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 主题资源表 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2021-02-28
 */
@Slf4j
@RestController
@RequestMapping("/")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("access/token")
    public BusinessResult<String> getToken(@RequestParam("appFlag") String appFlag) {
        return authService.getToken(appFlag);
    }

    @GetMapping("getData")
    public BusinessResult<List<Object>> getData() {
        return authService.getData();
    }
}

