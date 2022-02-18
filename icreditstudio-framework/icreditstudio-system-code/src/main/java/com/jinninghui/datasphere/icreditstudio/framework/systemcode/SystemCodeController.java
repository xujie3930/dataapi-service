package com.jinninghui.datasphere.icreditstudio.framework.systemcode;

import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liyanhui
 */
@RestController
@RequestMapping("system")
public class SystemCodeController {

    private final SystemCodeService systemCodeService;

    public SystemCodeController(@Autowired SystemCodeService systemCodeService) {
        this.systemCodeService = systemCodeService;
    }

    @RequestMapping(value = "translate/{code}", method = RequestMethod.GET)
    public BusinessResult<Map<String, String>> translate(@PathVariable(value = "code") String code){
        Map<String, String> result = new HashMap<>(2);
        result.put("code", code);
        result.put("msg", systemCodeService.getMessageOptional(code).orElse(""));
        return BusinessResult.success(result);
    }

    @RequestMapping(value = "translate/codes", method = RequestMethod.GET)
    public BusinessResult<Map<String, String>> codes(){
        return BusinessResult.success(systemCodeService.getSystemCodeMap());
    }
}
