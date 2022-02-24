package com.jinninghui.datasphere.icreditstudio.dataapi.feign;

import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DatasourceDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.DataSourcesListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.TableNameListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DataSourceInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.ConnectionInfoVO;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
@FeignClient("datasource")
public interface DatasourceFeignClient {

    @PostMapping("/datasource/getConnectionInfo")
    BusinessResult<ConnectionInfoVO> getConnectionInfo(@RequestBody DataSourceInfoRequest request);

    @PostMapping("/datasource/getDataSourcesList")
    BusinessResult<List<Map<String, Object>>> getDataSourcesList(@RequestBody DataSourcesListRequest request);

    /**
     * 根据数据源类型，查询数据源列表
     * @param
     * @return
     */
    @PostMapping("/datasource/getTableByDatasourceId")
    BusinessResult<List<Map<String, String>>> getTableNameList(@RequestBody TableNameListRequest request);

    /**
     * 根据主键id查询数据源信息
     */
    @GetMapping("/datasource/info/{id}")
    BusinessResult<DatasourceDetailResult> info(@PathVariable("id") String id);

}
