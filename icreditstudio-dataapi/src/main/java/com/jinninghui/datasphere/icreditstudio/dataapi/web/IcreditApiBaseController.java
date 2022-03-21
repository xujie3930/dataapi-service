package com.jinninghui.datasphere.icreditstudio.dataapi.web;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.FieldInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.QuerySqlCheckType;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiNameAndIdListResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiSaveResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiDetailResult;
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
    @ResultReturning
    public BusinessResult<BusinessPageResult> list(@RequestBody ApiBaseListRequest request) {
        return apiBaseService.getList(request);
    }

    @PostMapping("/datasourceApi/create")
    public BusinessResult<ApiSaveResult> createApi(@RequestHeader(value = "userId") String userId, @RequestBody DatasourceApiSaveRequest apiSaveRequest){
        DatasourceApiSaveParam param = new DatasourceApiSaveParam();
        BeanUtils.copyProperties(apiSaveRequest, param);
        return apiBaseService.createApi(userId, param);
    }

    @PostMapping("/generate/apiPath")
    public BusinessResult<String> generateApiPath(){
        return BusinessResult.success(RandomStringUtils.randomAlphabetic(16));
    }

    @PostMapping("/detail")
    @ResultReturning
    public BusinessResult<ApiDetailResult> detail(@RequestBody ApiBaseDetailRequest request) {
        return apiBaseService.detail(request.getId());
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

    @PostMapping("/checkApiPath")
    public BusinessResult<Boolean> checkApiPath(@RequestBody CheckApiPathRequest request){
        return apiBaseService.checkApiPath(request);
    }

    @PostMapping("/checkApiName")
    public BusinessResult<Boolean> checkApiName(@RequestBody CheckApiNameRequest request){
        return apiBaseService.checkApiName(request);
    }

    @PostMapping("/checkQuerySql")
    public BusinessResult<String> checkQuerySql(@RequestBody CheckQuerySqlRequest request){
        return BusinessResult.success((String) apiBaseService.checkQuerySql(request, "", 0, QuerySqlCheckType.NEED_NOT_GET_TABLE_FIELD.getCode()));
    }

    @PostMapping("/publishOrStop")
    public BusinessResult<Boolean> publishOrStop(@RequestHeader(value = "userId") String userId, @RequestBody ApiPublishRequest request){
        return apiBaseService.publishOrStop(userId, request);
    }

    @PostMapping("/getApiList")
    public BusinessResult<List<ApiNameAndIdListResult>> getApiList(@RequestBody ApiNameAndIdListRequest request){
        return apiBaseService.getApiByApiGroupId(request);
    }

    @PostMapping("/datasourceApi/createAndPublish")
    public BusinessResult<ApiSaveResult> createAndPublish(@RequestHeader(value = "userId") String userId, @RequestBody DatasourceApiSaveRequest apiSaveRequest){
        DatasourceApiSaveParam param = new DatasourceApiSaveParam();
        BeanUtils.copyProperties(apiSaveRequest, param);
        return apiBaseService.createAndPublish(userId, param);
    }

}

