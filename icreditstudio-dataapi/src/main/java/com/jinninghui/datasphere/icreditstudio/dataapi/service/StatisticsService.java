package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsApiTopResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsAppTopResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsResult;

import java.util.List;

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

    /**
     * 统计所有应用的总调用次数，按调用次数排序
     * @author  maoc
     * @create  2022/5/20 11:35
     * @desc
     **/
    List<StatisticsAppTopResult> appTopView();

    /**
     * 统计所有接口的总调用次数，按调用次数排序
     * @author  maoc
     * @create  2022/6/22 15:02
     * @desc
     **/
    List<StatisticsApiTopResult> apiTopView();

    //List<Map<String, String>> xnMap();
}
