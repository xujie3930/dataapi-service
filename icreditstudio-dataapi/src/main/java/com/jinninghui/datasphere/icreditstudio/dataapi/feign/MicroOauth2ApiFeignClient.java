package com.jinninghui.datasphere.icreditstudio.dataapi.feign;

import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.CommonResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.InternalUserInfoVO;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @author xujie
 * @description iCreditBanner类
 * @create 2021-08-19 14:17
 **/
@Component
@org.springframework.cloud.openfeign.FeignClient("micro-oauth2-api")
public interface MicroOauth2ApiFeignClient {

    @GetMapping("/sys/user/internal/info/{id}")
    CommonResult<InternalUserInfoVO> info(
            @PathVariable("id") String id);

    @PostMapping("/sys/user/internal/selUser/batch")
    CommonResult<Map<String, String>> selUserBatch(
            @RequestBody List<String> userIds);
}
