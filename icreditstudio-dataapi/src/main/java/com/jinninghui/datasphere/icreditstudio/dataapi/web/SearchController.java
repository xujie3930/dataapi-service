package com.jinninghui.datasphere.icreditstudio.dataapi.web;


import com.jinninghui.datasphere.icreditstudio.dataapi.common.validate.ResultReturning;
import com.jinninghui.datasphere.icreditstudio.dataapi.kafaka.KafkaConsumer;
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

import javax.servlet.http.HttpServletRequest;
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
    public BusinessResult<List<StatisticsAppTopResult>> appTopView(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
        return BusinessResult.success(statisticsService.appTopView(pageNum, pageSize));
    }

    @Autowired
    private KafkaConsumer kafkaConsumer;
    @GetMapping ("/t")
    public BusinessResult<Boolean> t(HttpServletRequest request) {
        return BusinessResult.success(kafkaConsumer.addAppUsedCount(request.getParameter("appId")));
    }
}

