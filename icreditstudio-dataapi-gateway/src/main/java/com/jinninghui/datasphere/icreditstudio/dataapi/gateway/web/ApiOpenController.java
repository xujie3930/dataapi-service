package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.AuthService;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
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
@RequestMapping("/openapi")
public class ApiOpenController {

    @Autowired
    private AuthService authService;

    @GetMapping("/{version}/{path}")
    public BusinessResult<List<Object>> getData(@PathVariable("version") String version, @PathVariable("path") String path) {
        return authService.getData(version, path);
    }
}

