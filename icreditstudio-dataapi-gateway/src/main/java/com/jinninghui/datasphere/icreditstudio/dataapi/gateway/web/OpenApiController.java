package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.web;

import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.service.OpenApiService;
import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.utils.MapUtils;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

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
public class OpenApiController {

    @Autowired
    private OpenApiService openApiService;

    @GetMapping("/{version}/{path}")
    public BusinessResult<Object> getData(@PathVariable("version") String version, @PathVariable("path") String path, HttpServletRequest request) throws UnsupportedEncodingException {
        String queryString = request.getQueryString();
        Map<String, Object> paramMap = MapUtils.convertParams(queryString);
        return openApiService.getData(version, path, paramMap);
    }

    @PostMapping("/post/{version}/{path}")
    public BusinessResult<Object> postGetData(@PathVariable("version") String version, @PathVariable("path") String path, HttpServletRequest request) throws UnsupportedEncodingException {
        Map params = (Map) request.getAttribute(MapUtils.PARAM_ATTRIBUTE);
        request.removeAttribute(MapUtils.PARAM_ATTRIBUTE);
        return openApiService.getData(version, path, params);
    }
}

