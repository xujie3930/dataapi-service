package com.jinninghui.datasphere.icreditstudio.dataapi.kafaka;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.CallStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiLogEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiLogMapper;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.impl.StatisticsServiceImpl;
import com.jinninghui.datasphere.icreditstudio.framework.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xujie
 * @description kafka消费者
 * @create 2022-03-02 16:57
 **/
@Component
@Slf4j
public class KafkaConsumer {

    @Resource
    private IcreditApiLogMapper apiLogMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisUtils redisUtils;
    @Value("${system.prop.app.used.count.redis.key}")
    private String appUsedCount;

    @KafkaListener(groupId = "test", topics = "#{'${kafkaConsumer.topic}'}")
    public void topic_test(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        Optional message = Optional.ofNullable(record.value());
        log.info("获取Topic:{}的消息:{}进行消费", topic, message);
        try {
            if (message.isPresent()) {
                Object msg = message.get();
                ApiLogInfo logInfo = JSON.parseObject(String.valueOf(msg), ApiLogInfo.class);
                //true-第一次插入，false-第二次插入
                if(redisTemplate.opsForValue().setIfAbsent(logInfo.getTraceId(), logInfo)){
                    IcreditApiLogEntity apiLogEntity = new IcreditApiLogEntity();
                    BeanUtils.copyProperties(logInfo, apiLogEntity);
                    apiLogMapper.insert(apiLogEntity);
                }else{
                    if (!CallStatusEnum.CALL_ON.getCode().equals(logInfo.getCallStatus())){
                        IcreditApiLogEntity logApiLogEntity = apiLogMapper.findByTraceId(logInfo.getTraceId());
                        String logId = logApiLogEntity.getId();
                        String createBy = logApiLogEntity.getCreateBy();
                        BeanUtils.copyProperties(logInfo, logApiLogEntity);
                        logApiLogEntity.setId(logId);
                        logApiLogEntity.setCreateBy(createBy);
                        apiLogMapper.updateById(logApiLogEntity);
                    }
                    redisTemplate.delete(logInfo.getTraceId());
                }
                //修改redis引用调用数
                this.addAppUsedCount(logInfo.getAppId());
                log.info("topic_test 消费了： Topic:" + topic + ",Message:" + msg);
            }
        } catch (BeansException e) {
            log.error("消费异常:Topic:{},Message:{}", topic, record.value());
        } finally {
            ack.acknowledge();
        }
    }

    public boolean addAppUsedCount(String appId){
        if(StringUtils.isEmpty(appId)){
            return false;
        }
        //操作redis
        Object useCountObj = redisUtils.hget(appUsedCount, appId);
        Integer useCount = (null==useCountObj || "null".equals(useCountObj+""))?null:Integer.valueOf(useCountObj+"");
        if(null==useCount){
            //使用锁，防止同步操作时数据错乱
            synchronized (StatisticsServiceImpl.updateRedisUsedCountLock){
                if(null==redisUtils.hget(appUsedCount, appId)){
                    //查询数据库，回写redis
                    List<String> querys = new ArrayList<>(2);
                    querys.add(appId);
                    List<Map<String, Object>> dbdata = apiLogMapper.queryUsedCountByAppIds(querys);
                    useCount = (null==dbdata || null==dbdata.get(0) || null==dbdata.get(0).get("nums"))?0:Integer.valueOf(dbdata.get(0).get("nums")+"");
                    //回写redis
                    redisUtils.hset(appUsedCount, appId, useCount+1);
                }else{
                    //+1
                    Long hincrby = redisUtils.hincrby(appUsedCount, appId, 1);
                    System.out.println(hincrby);
                }
            }
        }else{
            //+1
            Long hincrby = redisUtils.hincrby(appUsedCount, appId, 1);
            System.out.println(hincrby);
        }
        return true;
    }
}
