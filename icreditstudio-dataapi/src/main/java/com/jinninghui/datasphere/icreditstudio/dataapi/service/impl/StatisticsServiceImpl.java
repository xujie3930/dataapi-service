package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.AppEnableEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.DelFlagEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiPublishStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseHiService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.StatisticsService;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsApiTopResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsAppTopResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.StatisticsResult;
import com.jinninghui.datasphere.icreditstudio.framework.utils.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;

/**
 * @author xujie
 * @description 统计服务类
 * @create 2022-04-29 15:45
 **/
@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private IcreditApiBaseService icreditApiBaseService;
    @Autowired
    private IcreditApiBaseMapper apiBaseMapper;
    @Autowired
    private IcreditApiBaseHiMapper apiBaseHiMapper;
    @Autowired
    private IcreditApiBaseHiService apiBaseHiService;
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
    @Value("${system.prop.api.used.count.redis.key}")
    private String apiUsedCount;
    @Value("${system.prop.api.last.time.redis.key}")
    private String apiLastTime;

    public static final String updateRedisUsedCountLock = "updateRedisUsedCountLock";
    public static final Object updateApiRedisUsedCountLock = new Object();

    @Override
    public StatisticsResult statistics() {
        StatisticsResult result = new StatisticsResult();
        Long publishInterfaceCount = apiBaseHiMapper.getCountByPublishAndDelFlag(ApiPublishStatusEnum.PUBLISHED.getCode(), DelFlagEnum.ENA_BLED.getCode());
//        Long publishInterfaceCount = apiBaseMapper.getCountByPublishAndDelFlag(ApiPublishStatusEnum.PUBLISHED.getCode(), DelFlagEnum.ENA_BLED.getCode());
        result.setPublishInterfaceCount(publishInterfaceCount);
        Long interfaceCount = apiBaseHiMapper.getCountByPublishAndDelFlag(null, DelFlagEnum.ENA_BLED.getCode());
//        Long interfaceCount = apiBaseMapper.getCountByPublishAndDelFlag(null, DelFlagEnum.ENA_BLED.getCode());
        result.setInterfaceCount(interfaceCount);
        Long authInterfaceCount = authMapper.getApiAuthCount(DelFlagEnum.ENA_BLED.getCode());
        result.setAuthInterfaceCount(authInterfaceCount);
        Long appAuthCount = authMapper.getAppAuthCount(DelFlagEnum.ENA_BLED.getCode());
        result.setAppAuthCount(appAuthCount);
        Long enableAppCount = appMapper.getCountByEnableAndDelFlag(AppEnableEnum.ENABLE.getCode(), DelFlagEnum.ENA_BLED.getCode());
        result.setEnableAppCount(enableAppCount);
        Long appCount = appMapper.getCountByEnableAndDelFlag(null, DelFlagEnum.ENA_BLED.getCode());
        result.setAppCount(appCount);
        Long newlyInterfaceCount = apiBaseMapper.newlyDayList(new Date());
        result.setNewlyInterfaceCount(newlyInterfaceCount);
        return result;
    }

    /*private List<Map<String, Object>> getAppApiCountList(){
        long a = System.currentTimeMillis();
        List<Map<String, Object>> results = new ArrayList<>();
        String cacheData = (String) redisUtils.get(appApiAuth);
        xnMap.get("8").addAndGet(System.currentTimeMillis()-a);
        a = System.currentTimeMillis();
        if(StringUtils.isEmpty(cacheData)){
            List<Map<String, Object>> appApiCountList = appMapper.getAppApiCountList();
            if(null!=appApiCountList && !appApiCountList.isEmpty()){
                List<Map<String, Object>> redisSave = new ArrayList<>();
                appApiCountList.stream().forEach(appApiCount->{
                    redisSave.add(appApiCount);
                    results.add(appApiCount);

                });
                redisUtils.set(appApiAuth, JSONArray.toJSONString(redisSave), 60l);
                //redisUtils.expire(appApiAuth, 60);
            }
            xnMap.get("9").addAndGet(System.currentTimeMillis()-a);
        }else{
            JSONArray.parseArray(cacheData).stream().forEach(cache->{
                results.add((Map<String, Object>) cache);
            });
            xnMap.get("10").addAndGet(System.currentTimeMillis()-a);
        }
        return results;
    }*/

    @Override
    public List<StatisticsAppTopResult> appTopView() {
        List<Map<String, Object>> appApiCountList = appMapper.getAppApiCountList();
        //List<Map<String, Object>> appApiCountList = this.getAppApiCountList();
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
                        redisUtils.hincrby(appUsedCount, noappId, 0l);
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

    /**
     * 获取上次api使用数量统计时间
     * 返回-1：无需同步，返回0：全量同步，放回整数：上次同步时间
     * @author  maoc
     * @create  2022/6/23 15:19
     * @desc
     **/
    private Long apiCountNeedUpdate(){
        boolean exists = redisUtils.exists(apiUsedCount).booleanValue();
        if(!exists){
            return 0l;
        }
        //获取上次同步时间
        Object lasttimeo = redisUtils.get(apiLastTime);
        if(null==lasttimeo){
            return 0l;
        }
        Long lasttime = Long.valueOf(lasttimeo+"");
        if(System.currentTimeMillis()-lasttime.longValue()<60000){
            //小于一分钟，不同步
            return -1l;
        }
        //获取最新的api
        final QueryWrapper wrappers = new QueryWrapper<IcreditApiBaseEntity>();
        wrappers.eq("del_flag", 0);
        wrappers.gt("create_time", new Date(Long.valueOf(lasttime+"")));
        int newCount = icreditApiBaseService.count(wrappers);
        redisUtils.set(apiLastTime, System.currentTimeMillis());
        return newCount>0?lasttime:-1l;
    }

    private void initApiRedisData(){
        Long lastTime1 = apiCountNeedUpdate();
        if(lastTime1.longValue()>=0){
            synchronized (updateApiRedisUsedCountLock){
                Long lastTime2 = apiCountNeedUpdate();
                if(lastTime2.longValue()>=0){
                    //查询所有接口
                    Map<String, Map<String, Object>> apiMap = new HashMap<>();
                    List<ZSetOperations.TypedTuple<Object>> apiCountList = new ArrayList<>();
                    Set<ZSetOperations.TypedTuple<Object>> apiCountRedisSet = new HashSet<>();

                    final Map<String, Object> paramsMap = new HashMap<>(4);
                    final QueryWrapper wrappers = new QueryWrapper<IcreditApiBaseEntity>();
                    wrappers.eq("del_flag", 0);
                    /*if(lastTime2.longValue()>0){
                        Date lt2 = new Date(lastTime2);
                        wrappers.gt("create_time", lt2);
                        paramsMap.put("createTimeGt", lt2);
                    }*/
                    int size=100;
                    int count = icreditApiBaseService.count(wrappers);
                    int page = (count % size == 0?count/size:count/size+1);
                    if(count<=0){
                        return ;
                    }

                    //查询redis所有数据
                    Map<String, DefaultTypedTuple<Object>> existMap = new HashMap<>();
                    Set<Object> apiCount = redisUtils.zrevrange(apiUsedCount, 0, -1, true);
                    if(null!=apiCount && !apiCount.isEmpty()){
                        apiCount.stream().forEach(api->{
                            DefaultTypedTuple<Object> tt = (DefaultTypedTuple<Object>) api;
                            existMap.put((String) tt.getValue(), tt);
                        });
                    }

                    paramsMap.put("size", size);
                    for(int i=0;i<page;i++){
                        paramsMap.put("start", i*100);
                        final Map<String, ZSetOperations.TypedTuple<Object>> apiCountMap = new HashMap<>();
                        List<Map<String, Object>> apis = apiBaseMapper.queryApiInBiUsedCount(paramsMap);
                        if(null!=apis && !apis.isEmpty()){
                            for(Map<String, Object> api:apis){
                                String id = (String) api.get("id");
                                DefaultTypedTuple<Object> dtt = null;//分数就是调用次数，默认0

                                DefaultTypedTuple<Object> exist = existMap.get(id);
                                if(lastTime2.longValue()==0 || null==exist){
                                    //全量或缓存不存在
                                    dtt = new DefaultTypedTuple<>(id, 0d);
                                    apiCountMap.put(id, dtt);
                                }else{
                                    //已经存在
                                    dtt = new DefaultTypedTuple<>(id, exist.getScore());
                                }

                                apiCountRedisSet.add(dtt);
                                apiCountList.add(dtt);
                                apiMap.put(id, api);
                            }
                            if(apiCountMap.isEmpty()){
                                continue;
                            }
                            //查询接口调用次数
                            List<Map<String, Object>> dbcounts = apiLogMapper.queryUsedCountByApiIds(apiCountMap.keySet());
                            if(null!=dbcounts && !dbcounts.isEmpty()){
                                dbcounts.stream().forEach(dbcount->{
                                    String apiId = (String) dbcount.get("apiId");
                                    Double dbnum = (null==dbcount.get("nums")?0d:((Long) dbcount.get("nums")).doubleValue());
                                    if(null!=apiId && null!=dbnum){
                                        ZSetOperations.TypedTuple<Object> dtt = apiCountMap.get(apiId);
                                        try {
                                            //设置调用次数
                                            //ZSetOperations.TypedTuple类分数数据后就不可修改，故此处采用反射修改值
                                            Field scoref = dtt.getClass().getDeclaredField("score");
                                            scoref.setAccessible(true);
                                            scoref.set(dtt, dbnum);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }

                    //做二级排序，根据应用名
                    Comparator compareIns = Collator.getInstance(java.util.Locale.CHINA);
                    apiCountList.sort((p1, p2)-> {
                        int perCom = p2.getScore().compareTo(p1.getScore());
                        if (perCom == 0) {
                            //找到分数相同的
                            String id1 = (String) p1.getValue();
                            String id2 = (String) p2.getValue();
                            Map<String, Object> api1 = apiMap.get(id1);
                            Map<String, Object> api2 = apiMap.get(id2);
                            String name1 = (String) api1.get("name");
                            String name2 = (String) api2.get("name");
                            int compare = compareIns.compare(name1, name2);
                            return compare;
                        } else {
                            return perCom;
                        }
                    });

                    //排好序的，设置分数
                    for(int i=0;i<apiCountList.size();i++){
                        ZSetOperations.TypedTuple<Object> zt = apiCountList.get(i);
                        String id = (String) zt.getValue();
                        double score = zt.getScore();
                        Class<? extends ZSetOperations.TypedTuple> clazz = zt.getClass();
                        try {
                            Field field = clazz.getDeclaredField("score");
                            field.setAccessible(true);
                            field.set(zt, score+Double.valueOf("0."+(Integer.MAX_VALUE-i)));
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    //写入redis
                    if(!apiCountRedisSet.isEmpty()){
                        redisUtils.zadd(apiUsedCount, apiCountRedisSet);
                        redisUtils.set(apiLastTime, System.currentTimeMillis());
                    }
                }

            }
        }
    }

    //@Override
    public List<StatisticsApiTopResult> apiTopView2() {
        this.initApiRedisData();
        //有数据了
        //获取前100名
        Set<Object> apiCount = redisUtils.zrevrange(apiUsedCount, 0, 99, true);
        if(null==apiCount || apiCount.isEmpty()){
            return new ArrayList<>(0);
        }
        List<StatisticsApiTopResult> resultList = new ArrayList<>();
        Map<String, StatisticsApiTopResult> resultMap = new HashMap<>();
        for(Object api:apiCount){
            StatisticsApiTopResult tr = new StatisticsApiTopResult();
            DefaultTypedTuple<Object> tt = (DefaultTypedTuple<Object>) api;
            tr.setApiId((String) tt.getValue());
            tr.setApiUsedCount(tt.getScore()==null?0: (int) tt.getScore().doubleValue());//有隐患
            resultList.add(tr);
            resultMap.put(tr.getApiId(), tr);
        }

        //做二级排序，获取最后一个成员
        final Map<String, Object> paramsMap = new HashMap<>(2);
        paramsMap.put("ids", resultMap.keySet());
        List<Map<String, Object>> apiBases = apiBaseMapper.queryApiInBiUsedCount(paramsMap);
        if(null!=apiBases && !apiBases.isEmpty()){
            apiBases.stream().forEach(apiBase->{
                Object interfaceSourceo = apiBase.get("interfaceSource");
                Object typeo = apiBase.get("type");
                String id = (String) apiBase.get("id");
                String name = (String) apiBase.get("name");
                Integer type = (null==typeo?null:(typeo instanceof java.lang.Long?((Long) typeo).intValue():(Integer) typeo));
                Integer interfaceSource = (null==interfaceSourceo?null:(null!= interfaceSourceo && interfaceSourceo instanceof java.lang.Long?((Long) interfaceSourceo).intValue():(Integer) interfaceSourceo));
                StatisticsApiTopResult result = resultMap.get(id);
                result.setApiName(name);
                result.setApiType(type);
                result.setApiSource(interfaceSource);
            });
        }
        //排序
        /*Comparator compareIns = Collator.getInstance(java.util.Locale.CHINA);
        resultList.sort((p1, p2)-> {
            int perCom = p2.getApiUsedCount().compareTo(p1.getApiUsedCount());
            if (perCom == 0) {
                return compareIns.compare(p1.getApiName(), p2.getApiName());}
            else {
                return perCom;
            }
        });
        //final List<StatisticsAppTopResult> results = resultList.stream().sorted(Comparator.comparing(StatisticsAppTopResult::getUseApiCount).reversed().thenComparing(StatisticsAppTopResult::getAppName)).collect(Collectors.toList());
        for(int i=0;i<resultList.size();i++){
            resultList.get(i).setSort(i+1);
        }*/
        return resultList;
    }

    @Override
    public List<StatisticsApiTopResult> apiTopView() {

        Long lastTime1 = apiCountNeedUpdate();
        if(lastTime1.longValue()>=0){
            synchronized (apiUsedCount){
                Long lastTime2 = apiCountNeedUpdate();
                if(lastTime2.longValue()>=0){
                    //查询所有接口
                    Set<ZSetOperations.TypedTuple<Object>> apiCountRedisSet = new HashSet<>();
                    final Map<String, ZSetOperations.TypedTuple<Object>> apiCountMap = new HashMap<>();
                    final Map<String, Object> paramsMap = new HashMap<>(4);
                    final QueryWrapper wrappers = new QueryWrapper<IcreditApiBaseEntity>();
                    wrappers.eq("del_flag", 0);
                    if(lastTime2.longValue()>0){
                        Date lt2 = new Date(lastTime2);
                        wrappers.gt("create_time", lt2);
                        paramsMap.put("createTimeGt", lt2);
                    }
                    int size=100;
                    int count = icreditApiBaseService.count(wrappers);
                    int page = (count % size == 0?count/size:count/size+1);
                    if(count>0){
                        paramsMap.put("size", size);
                        for(int i=0;i<page;i++){
                            paramsMap.put("start", i*100);
                            List<Map<String, Object>> apis = apiBaseMapper.queryApiInBiUsedCount(paramsMap);
                            if(null!=apis && !apis.isEmpty()){
                                for(Map<String, Object> api:apis){
                                    String id = (String) api.get("id");
                                    DefaultTypedTuple<Object> dtt = new DefaultTypedTuple<>(id, 0d);
                                    apiCountMap.put(id, dtt);
                                    apiCountRedisSet.add(dtt);
                                }
                                //查询接口调用次数
                                List<Map<String, Object>> dbcounts = apiLogMapper.queryUsedCountByApiIds(apiCountMap.keySet());
                                if(null!=dbcounts && !dbcounts.isEmpty()){
                                    dbcounts.stream().forEach(dbcount->{
                                        String apiId = (String) dbcount.get("apiId");
                                        Double dbnum = (null==dbcount.get("nums")?0d:((Long) dbcount.get("nums")).doubleValue());
                                        if(null!=apiId && null!=dbnum){
                                            ZSetOperations.TypedTuple<Object> dtt = apiCountMap.get(apiId);
                                            try {
                                                Field scoref = dtt.getClass().getDeclaredField("score");
                                                scoref.setAccessible(true);
                                                scoref.set(dtt, dbnum);
                                            } catch (NoSuchFieldException e) {
                                                e.printStackTrace();
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }

                    //写入redis
                    if(!apiCountRedisSet.isEmpty()){
                        redisUtils.zadd(apiUsedCount, apiCountRedisSet);
                        redisUtils.set(apiLastTime, System.currentTimeMillis());
                    }
                }

            }
        }

        //有数据了
        //获取前100名
        Set<Object> apiCount = redisUtils.zrevrange(apiUsedCount, 0, 99, true);
        if(null==apiCount || apiCount.isEmpty()){
            return new ArrayList<>(0);
        }
        int sort = 1;
        List<StatisticsApiTopResult> resultList = new ArrayList<>();
        Map<String, StatisticsApiTopResult> resultMap = new HashMap<>();
        for(Object api:apiCount){
            StatisticsApiTopResult tr = new StatisticsApiTopResult();
            DefaultTypedTuple<Object> tt = (DefaultTypedTuple<Object>) api;
            tr.setApiId((String) tt.getValue());
            tr.setApiUsedCount(tt.getScore()==null?0: (int) tt.getScore().doubleValue());//有隐患
            tr.setSort(sort++);
            resultList.add(tr);
            resultMap.put(tr.getApiId(), tr);
        }

        final Map<String, Object> paramsMap = new HashMap<>(2);
        paramsMap.put("ids", resultMap.keySet());
        List<Map<String, Object>> apiBases = apiBaseMapper.queryApiInBiUsedCount(paramsMap);
        if(null!=apiBases && !apiBases.isEmpty()){
            apiBases.stream().forEach(apiBase->{
                Object interfaceSourceo = apiBase.get("interfaceSource");
                Object typeo = apiBase.get("type");
                String id = (String) apiBase.get("id");
                String name = (String) apiBase.get("name");
                Integer type = (null==typeo?null:(typeo instanceof java.lang.Long?((Long) typeo).intValue():(Integer) typeo));
                Integer interfaceSource = (null==interfaceSourceo?null:(null!= interfaceSourceo && interfaceSourceo instanceof java.lang.Long?((Long) interfaceSourceo).intValue():(Integer) interfaceSourceo));
                StatisticsApiTopResult result = resultMap.get(id);
                result.setApiName(name);
                result.setApiType(type);
                result.setApiSource(interfaceSource);
            });
        }
        return resultList;
    }
}
