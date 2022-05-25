package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditWorkFlowService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.StatisticsService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.CharacterUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.WorkFlowSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsAppTopResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.WorkFlowResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 注册API 前端控制器
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@RestController
@RequestMapping("/")
public class SearchController {

    @Autowired
    private IcreditWorkFlowService icreditWorkFlowService;
    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/search")
    @ResultReturning
    public BusinessResult<List<WorkFlowResult>> searchFromName(@RequestBody WorkFlowSaveRequest request) {
        List<WorkFlowResult> workFlowResults = icreditWorkFlowService.searchFromName(request);
        return BusinessResult.success(workFlowResults);
    }

    @GetMapping ("/generate/random")
    public BusinessResult<String> generateRandom(@RequestParam(value = "len", defaultValue = "16") Integer len) {
        String randomString = CharacterUtils.getRandomString(len);
        return BusinessResult.success(randomString);
    }

    @GetMapping ("/statistics")
    public BusinessResult<StatisticsResult> statistics() {
        return BusinessResult.success(statisticsService.statistics());
    }

    @GetMapping ("/appTopView")
    public BusinessResult<List<StatisticsAppTopResult>> appTopView() {
        return BusinessResult.success(statisticsService.appTopView());
    }

    /*@Autowired
    private IcreditAppService appService;
    @Autowired
    private IcreditApiBaseService apiBaseService;
    @Autowired
    private IcreditAuthService authService;
    @GetMapping ("/t")
    public BusinessResult<Boolean> t(HttpServletRequest request) {


        List<IcreditAppEntity> apps = new ArrayList<>();
        List<IcreditApiBaseEntity> apis = new ArrayList<>();
        for(int i=200;i<210;i++){
            IcreditApiBaseEntity api = new IcreditApiBaseEntity();
            IcreditAppEntity app  = new IcreditAppEntity();
            app.setCertificationType(0);
            app.setIsEnable(1);
            app.setName("mao_test_app_"+(i+1));
            app.setSecretContent("9xanKIIqBuNo2HHs");
            app.setAppGroupId("1506112839704748033");
            app.setPeriod(8);
            app.setDelFlag(0);
            app.setTokenType(1);
            app.setAppGroupId(app.getName());
            appService.save(app);
            System.out.println(app.getId());
            api.setType(1);
            api.setName("mao_test_api_"+(i+1));
            api.setPath("mSxlCKjKBgCfrYyn");
            api.setRequestType("GET");
            api.setResponseType("JSON");
            api.setApiGroupId("1508748961916215298");
            api.setPublishStatus(2);
            api.setApiVersion(1);
            api.setDelFlag(0);
            apiBaseService.save(api);
            System.out.println(api.getId());

            apps.add(app);
            apis.add(api);
        }

        for(int i=0;i<apps.size();i++){
            IcreditAppEntity app = apps.get(i);
            List<IcreditAuthEntity> saves = new ArrayList<>(100);
            for(int j=0;j<apis.size();j++){
                IcreditApiBaseEntity api = apis.get(j);
                IcreditAuthEntity auth = new IcreditAuthEntity();
                auth.setRemark("maoshi_test_auth_"+(i+201)+"_"+(j+201));
                auth.setApiId(api.getId());
                auth.setAppId(app.getId());
                auth.setDelFlag(0);
                saves.add(auth);
            }
            authService.saveBatch(saves);
        }

        return BusinessResult.success(true);
    }*/
}

