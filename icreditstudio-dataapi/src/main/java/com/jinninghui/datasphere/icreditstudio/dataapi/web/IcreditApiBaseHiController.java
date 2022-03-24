package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseHiService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiBaseHiDetailRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiHisDelRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiHistoryListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.DatasourceApiSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiHistoryListResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiSaveResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * API表基础信息历史版本表 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/apiHistory")
public class IcreditApiBaseHiController {

    @Resource
    private IcreditApiBaseHiService apiBaseHiService;

    @PostMapping("/list")
    @ResultReturning
    public BusinessResult<BusinessPageResult<ApiHistoryListResult>> list(@RequestBody ApiHistoryListRequest request){
        return apiBaseHiService.getList(request);
    }

    @PostMapping("/info")
    public BusinessResult<ApiDetailResult> info(@RequestBody ApiBaseHiDetailRequest request){
        return apiBaseHiService.info(request);
    }

    @PostMapping("/update")
    public BusinessResult<ApiSaveResult> update(@RequestHeader(value = "userId") String userId, @RequestBody DatasourceApiSaveRequest request){
        DatasourceApiSaveParam param = new DatasourceApiSaveParam();
        BeanUtils.copyProperties(request, param);
        return apiBaseHiService.updateApi(userId, param);
    }

    @PostMapping("/deleteBatch")
    BusinessResult<Boolean> deleteBatch(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody String[] apiHiIdsArry) {
        return apiBaseHiService.deleteBatch(userId, apiHiIdsArry);
    }

    @PostMapping("/delete")
    BusinessResult<Boolean> deleteBatch(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody ApiHisDelRequest request) {
        return apiBaseHiService.deleteById(userId, request.getId());
    }
}

