package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsAppTopResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 统计服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface StatisticsService  {

    StatisticsResult statistics();

    List<StatisticsAppTopResult> appTopView();

    List<Map<String, String>> xnMap();
}
