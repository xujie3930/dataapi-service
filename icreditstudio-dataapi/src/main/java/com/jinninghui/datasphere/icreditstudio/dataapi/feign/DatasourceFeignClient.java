package com.jinninghui.datasphere.icreditstudio.dataapi.feign;

import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DataSourceInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.ConnectionInfoVO;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient("datasource")
public interface DatasourceFeignClient {

    @PostMapping("/datasource/getConnectionInfo")
    BusinessResult<ConnectionInfoVO> getConnectionInfo(@RequestBody DataSourceInfoRequest request);

}
