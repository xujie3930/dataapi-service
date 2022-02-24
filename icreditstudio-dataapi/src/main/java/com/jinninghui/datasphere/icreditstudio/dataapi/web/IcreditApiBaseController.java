package com.jinninghui.datasphere.icreditstudio.dataapi.web;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.FieldInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        return apiBaseService.createDataSourceApi(userId, param);
    }

    @PostMapping("/generate/apiPath")
    public String generateApiPath(){
        return RandomStringUtils.randomAlphabetic(16);
    }

    @PostMapping("/getDatasourceListByType")
    BusinessResult<List<Map<String, Object>>> getDataSourcesList(@RequestBody DataSourcesListRequest request) {
        return apiBaseService.getDataSourcesList(request);
    }

    /**
     * 根据数据源类型，查询数据源列表
     * @param
     * @return
     */
    @PostMapping("/getTableByDatasourceId")
    BusinessResult<List<Map<String, String>>> getTableNameList(@RequestBody TableNameListRequest request) {
        return apiBaseService.getTableNameList(request);
    }

    @PostMapping("/getTableFieldList")
    public BusinessResult<List<FieldInfo>> getTableFieldList(@RequestBody TableFieldListRequest request){
        return apiBaseService.getTableFieldList(request);
    }

}

