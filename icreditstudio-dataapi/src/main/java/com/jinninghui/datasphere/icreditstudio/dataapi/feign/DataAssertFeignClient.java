package com.jinninghui.datasphere.icreditstudio.dataapi.feign;

import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient("dataresource-backend")
public interface DataAssertFeignClient {

    @GetMapping("/resource/table/getChineseName")
    BusinessResult<String> getChineseName(@RequestParam(value = "name") String name);

}
