package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppEnableEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiPublishStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiBaseMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiLogMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAppMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditAuthMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.StatisticsService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.HttpUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsAppTopResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.DateUtils;
import com.jinninghui.datasphere.icreditstudio.framework.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.Collator;
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
    @Value("${system.prop.app.api.auth.redis.key}")
    private String appApiAuth;

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

    private List<Map<String, Object>> getAppApiCountList(){
        List<Map<String, Object>> results = new ArrayList<>();
        List<Object> cacheData = redisUtils.lrange(appApiAuth, 0, -1);
        if(null==cacheData || cacheData.isEmpty()){
            List<Map<String, Object>> appApiCountList = appMapper.getAppApiCountList();
            if(null!=appApiCountList && !appApiCountList.isEmpty()){
                List<String> redisSave = new ArrayList<>();
                appApiCountList.stream().forEach(appApiCount->{
                    redisSave.add(JSON.toJSONString(appApiCount));
                    results.add(appApiCount);

                });
                redisUtils.lpush(appApiAuth, redisSave.toArray(new String[redisSave.size()]));
                redisUtils.expire(appApiAuth, 60);
            }

        }else{
            cacheData.stream().forEach(cache->{
                results.add(JSON.parseObject((String) cache));
            });
        }
        return results;
    }

    @Override
    public List<StatisticsAppTopResult> appTopView() {

        //List<Map<String, Object>> appApiCountList = appMapper.getAppApiCountList();
        List<Map<String, Object>> appApiCountList = this.getAppApiCountList();
        if(null==appApiCountList || appApiCountList.isEmpty()){
            return new ArrayList<>(0);
        }
        final List<StatisticsAppTopResult> resultList = new ArrayList<>();
        final Map<String, StatisticsAppTopResult> appMap = new HashMap<>();
        final Set<String> noappIds=new HashSet<>();
        //注意，hgetall有性能问题
        final Map<String, Object> counts = redisUtils.hgetall(appUsedCount);
        appApiCountList.stream().forEach(appApiCount->{
            StatisticsAppTopResult appTop = new StatisticsAppTopResult();
            String appId = (String) appApiCount.get("appId");
            String appName = (String) appApiCount.get("appName");
            Long apiCount = (appApiCount.get("apiCount") instanceof Long?(Long)appApiCount.get("apiCount"):(Integer)appApiCount.get("apiCount"));
            String redisCount = (counts.get(appId)==null||"".equals(counts.get(appId))?null:counts.get(appId)+"");
            boolean redisCountFlag = StringUtils.isEmpty(redisCount);
            appTop.setAppId(appId);
            appTop.setAppName(appName);
            appTop.setAuthApiCount(apiCount==null?0:apiCount.intValue());
            appTop.setUseApiCount(redisCountFlag?0:Integer.valueOf(redisCount));
            appMap.put(appId, appTop);
            resultList.add(appTop);
            if(redisCountFlag){
                noappIds.add(appId);
            }
        });

        /*Connection connection = DataSourceUtils.getConnection(dataSource);
        try{
            DatabaseMetaData metaData = connection.getMetaData();
            Properties clientInfo = connection.getClientInfo();
            ResultSet clientInfoProperties = metaData.getClientInfoProperties();
            ResultSet typeInfo = metaData.getTypeInfo();
            System.out.println(JSON.toJSONString(metaData));
        }catch (Exception ex){
            ex.printStackTrace();
        }*/

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
                        redisUtils.hincrby(appUsedCount, noappId, 0);
                    });
                }
            }
        }
        //排序
        Comparator compareIns = Collator.getInstance(java.util.Locale.CHINA);
        resultList.sort((p1, p2)-> {
            int perCom = Double.valueOf(p2.getUseApiCount()).compareTo(Double.valueOf(p1.getUseApiCount()));
            if (perCom == 0) {
                return compareIns.compare(p1.getAppName(), p2.getAppName());}
                else {
                    return perCom;
                }
        });
        //final List<StatisticsAppTopResult> results = resultList.stream().sorted(Comparator.comparing(StatisticsAppTopResult::getUseApiCount).reversed().thenComparing(StatisticsAppTopResult::getAppName)).collect(Collectors.toList());
        for(int i=0;i<resultList.size();i++){
            resultList.get(i).setSort(i+1);
        }
        return resultList;
    }

    public static void main(String[] s){
        String url = "http://127.0.0.1:9080/resource/table/info/baseInfo";
        Map<String, Object> map = new HashMap<>(8);
        Map<String, String> hea = new HashMap<>(4);
        map.put("tableName", "tag");
        map.put("datasourceId", "943883401306308608");
        map.put("type", 1);
        hea.put("Content-Type", "application/json");
        String send = JSON.toJSONString(map);
        /*for(int i=1;i<Integer.MAX_VALUE;i++){
            try {
                Thread.sleep(i<=6?i*1000:6000);
                System.out.println(DateUtils.formatDate(new Date(System.currentTimeMillis()))+":"+HttpUtils.sendPost(url, send, hea).length());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println(HttpUtils.sendPost(url, send, hea).length());
        }*/
        System.out.println(DateUtils.formatDate(new Date(System.currentTimeMillis()))+":"+HttpUtils.sendPost(url, send, hea).length());
        System.out.println(DateUtils.formatDate(new Date(System.currentTimeMillis()))+":"+HttpUtils.sendPost(url, send, hea).length());
    }
}
