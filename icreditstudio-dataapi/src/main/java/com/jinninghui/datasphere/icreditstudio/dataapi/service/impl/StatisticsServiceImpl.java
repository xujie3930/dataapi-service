package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppEnableEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiPublishStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiLogMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.StatisticsService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsAppTopResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xujie
 * @description 统计服务类
 * @create 2022-04-29 15:45
 **/
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private IcreditApiBaseMapper apiBaseMapper;
    @Autowired
    private IcreditAuthMapper authMapper;
    @Autowired
    private IcreditAppMapper appMapper;
    @Autowired
    private IcreditApiLogMapper apiLogMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Value("${system.prop.app.used.count.redis.key}")
    private String appUsedCount;
    public static final String updateRedisUsedCountLock = "updateRedisUsedCountLock";

    @Override
    public StatisticsResult statistics() {
        StatisticsResult result = new StatisticsResult();
        Long publishInterfaceCount = apiBaseMapper.getCountByPublishAndDelFlag(ApiPublishStatusEnum.PUBLISHED.getCode(), DelFlagEnum.ENA_BLED.getCode());
        result.setPublishInterfaceCount(publishInterfaceCount);
        Long interfaceCount = apiBaseMapper.getCountByPublishAndDelFlag(null, DelFlagEnum.ENA_BLED.getCode());
        result.setInterfaceCount(interfaceCount);
        Long authInterfaceCount = authMapper.getApiAuthCount(DelFlagEnum.ENA_BLED.getCode());
        result.setAuthInterfaceCount(authInterfaceCount);
        Long appAuthCount = authMapper.getAppAuthCount(DelFlagEnum.ENA_BLED.getCode());
        result.setAppAuthCount(appAuthCount);
        Long enableAppCount = appMapper.getCountByEnableAndDelFlag(AppEnableEnum.ENABLE.getCode(), DelFlagEnum.ENA_BLED.getCode());
        result.setEnableAppCount(enableAppCount);
        Long appCount = appMapper.getCountByEnableAndDelFlag(null, DelFlagEnum.ENA_BLED.getCode());
        result.setAppCount(appCount);
        return result;
    }

    @Override
    public List<StatisticsAppTopResult> appTopView() {

        List<Map<String, Object>> appApiCountList = appMapper.getAppApiCountList();
        if(null==appApiCountList || appApiCountList.isEmpty()){
            return new ArrayList<>(0);
        }
        List<StatisticsAppTopResult> resultList = new ArrayList<>();
        Map<String, StatisticsAppTopResult> appMap = new HashMap<>();
        Set<String> noappIds=new HashSet<>();
        //注意，hgetall有性能问题
        Map<String, Object> counts = redisUtils.hgetall(appUsedCount);

        for(int i=0;i<appApiCountList.size();i++){
            Map<String, Object> appApiCount = appApiCountList.get(i);
            StatisticsAppTopResult appTop = new StatisticsAppTopResult();
            String appId = (String) appApiCount.get("appId");
            String appName = (String) appApiCount.get("appName");
            Long apiCount = (Long) appApiCount.get("apiCount");
            String redisCount = (counts.get(appId)==null||"".equals(counts.get(appId))?null:counts.get(appId)+"");
            boolean redisCountFlag = StringUtils.isEmpty(redisCount);
            appTop.setSort(i+1);
            appTop.setAppId(appId);
            appTop.setAppName(appName);
            appTop.setAuthApiCount(apiCount==null?0:apiCount.intValue());
            appTop.setUseApiCount(redisCountFlag?0:Integer.valueOf(redisCount));
            appMap.put(appId, appTop);
            resultList.add(appTop);
            if(redisCountFlag){
                noappIds.add(appId);
            }
        }

        if(!noappIds.isEmpty()){
            //使用锁，防止同步操作时数据错乱
            synchronized (updateRedisUsedCountLock){
                //查询数据库回写redis
                List<Map<String, Object>> dbdatalist = apiLogMapper.queryUsedCountByAppIds(noappIds);
                if(null!=dbdatalist && !dbdatalist.isEmpty()){
                    dbdatalist.stream().forEach(dbdata->{
                        String dbappid = (String) dbdata.get("appId");
                        Integer dbcount = (null==dbdata.get("nums")?0:((Long) dbdata.get("nums")).intValue());
                        appMap.get(dbappid).setUseApiCount(dbcount);
                        redisUtils.hset(appUsedCount, dbappid, dbcount);
                        noappIds.remove(dbappid);
                    });
                }
                if(!noappIds.isEmpty()){
                    //如果存在没访问记录的引用，给默认访问记录
                    noappIds.stream().forEach(noappId->{
                        redisUtils.hset(appUsedCount, noappId, 0);
                    });
                }
            }
        }
        //排序
        return resultList.stream().sorted(Comparator.comparing(StatisticsAppTopResult::getUseApiCount).reversed()).collect(Collectors.toList());
    }
}
