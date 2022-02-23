package com.jinninghui.datasphere.icreditstudio.dataapi.web;

import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiBaseListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.DatasourceApiSaveRequest;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * API表基础信息表 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/apiBase")
public class IcreditApiBaseController {

    @Autowired
    private IcreditApiBaseService apiBaseService;

    @PostMapping("/list")
    public BusinessResult<BusinessPageResult> list(@RequestBody ApiBaseListRequest request) {
        return apiBaseService.getList(request);
    }

    @PostMapping("/datasourceApi/create")
    public BusinessResult<Boolean> createDataSourceApi(@RequestHeader(value = "userId") String userId, @RequestBody DatasourceApiSaveRequest apiSaveRequest){
        DatasourceApiSaveParam param = new DatasourceApiSaveParam();
        BeanUtils.copyProperties(apiSaveRequest, param);
        return apiBaseService.createDataSourceApi(param);
    }

    @PostMapping("/generate/apiPath")
    public String generateApiPath(){
        return RandomStringUtils.randomAlphabetic(16);
    }

}

