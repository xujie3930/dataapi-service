package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiLogService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.LogListQueryRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.LogListQueryResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 调用日志 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/log")
public class IcreditApiLogController {

    @Resource
    private IcreditApiLogService apiLogService;

    @PostMapping("/list")
    public BusinessResult<BusinessPageResult<LogListQueryResult>> list(@RequestBody LogListQueryRequest request){
        return apiLogService.getList(request);
    }

}

