package com.jinninghui.datasphere.icreditstudio.dataapi.web;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.ResourceCodeBean;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditAuthEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.AuthEffectiveTimeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditAuthService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiSaveResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthInfoResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.AuthListResult;
import com.jinninghui.datasphere.icreditstudio.framework.exception.interval.AppException;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.CollectionUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.GET;
import java.util.List;

/**
 * <p>
 * 授权表 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/auth")
public class IcreditAuthController {

    @Autowired
    private IcreditAuthService authService;

    @PostMapping("/save")
    BusinessResult<Boolean> saveDef(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody AuthSaveRequest request) {
        return authService.saveDef(userId, request);
    }

    @PostMapping("/config")
    BusinessResult<Boolean> configDef(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody AuthSaveRequest request) {
        return authService.configDef(userId, request);
    }

    /**
     * 根据API批量设置app配置
     * @author  maoc
     * @create  2022/6/2 15:55
     * @desc
     **/
    @PostMapping("/saveOuterApi")
    BusinessResult<ApiSaveResult> saveOuterApiDef(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody AuthSaveApiRequest request) {
        if(AuthEffectiveTimeEnum.SORT_TIME.getDurationType().equals(request.getCallCountType()) && request.getAllowCall() < 0){
            ResourceCodeBean.ResourceCode resourceCode20000036 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000036;
            return BusinessResult.fail(resourceCode20000036.getCode(), resourceCode20000036.getMessage());
        }
        /*if(StringUtils.isEmpty(request.getApiId())&&StringUtils.isEmpty(request.getPath())){
            ResourceCodeBean.ResourceCode resourceCode20000009 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000009;
            return BusinessResult.fail(resourceCode20000009.getCode(), resourceCode20000009.getMessage());
        }*/
        if (null==request.getAppIds()){
            ResourceCodeBean.ResourceCode resourceCode20000021 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000021;
            throw new AppException(resourceCode20000021.getCode(), resourceCode20000021.getMessage());
        }
        return authService.saveOuterApiDef(userId, request);
    }

    @PostMapping("/info")
    BusinessResult<AuthInfoResult> authInfo(@RequestBody AuthInfoRequest request) {
        return authService.authInfo(request);
    }

    /**
     * 获取应用授权列表
     * @author  maoc
     * @create  \ 16:32
     * @desc
     **/
    @PostMapping("/list")
    BusinessResult<BusinessPageResult<AuthListResult>> list(@RequestBody AuthListRequest request) {
        if(StringUtils.isEmpty(request.getAppId())){
            ResourceCodeBean.ResourceCode resourceCode20000021 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000021;
            return BusinessResult.fail(resourceCode20000021.getCode(), resourceCode20000021.getMessage());
        }
        if(null==request.getPeriodType() || request.getPeriodType().intValue()==2){
            request.setPeriodBegin(null);
            request.setPeriodEnd(null);
        }
        if(null!=request.getPeriodType() && request.getPeriodType().intValue()==1 && (null==request.getPeriodBegin() || null==request.getPeriodEnd())){
            //短期
            return BusinessResult.fail("", "授权时间不正确");
        }
        return BusinessResult.success(authService.authList(request));
    }

    @GetMapping("/queryApiAuthListByPath")
    BusinessResult<List<AuthListResult>> queryApiAuthListByPath(@RequestParam(value = "path") String path) {
        return BusinessResult.success(authService.queryApiAuthListByPath(path));
    }

    @PostMapping("/del")
    BusinessResult<Boolean> del(@RequestHeader(value = "userId", defaultValue = "910626036754939904") String userId, @RequestBody AuthDelRequest request) {
        //notblank和notempty失效
        if(StringUtils.isEmpty(request.getAppId())){
            ResourceCodeBean.ResourceCode resourceCode20000021 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000021;
            return BusinessResult.fail(resourceCode20000021.getCode(), resourceCode20000021.getMessage());
        }
        if(null==request.getAuthList() || request.getAuthList().isEmpty()){
            ResourceCodeBean.ResourceCode resourceCode20000056 = ResourceCodeBean.ResourceCode.RESOURCE_CODE_20000056;
            return BusinessResult.fail(resourceCode20000056.getCode(), resourceCode20000056.getMessage());
        }
        return authService.del(userId, request);
    }
}

