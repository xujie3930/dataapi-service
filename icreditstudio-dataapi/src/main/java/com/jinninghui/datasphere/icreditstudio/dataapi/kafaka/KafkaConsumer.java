package com.jinninghui.datasphere.icreditstudio.dataapi.kafaka;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.CallStatusEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiLogEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;
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

    @KafkaListener(groupId = "test", topics = "#{'${kafkaConsumer.topic}'}")
    public void topic_test(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        Optional message = Optional.ofNullable(record.value());
        log.info("获取Topic:{}的消息:{}进行消费", topic, message);
        try {
            if (message.isPresent()) {
                Object msg = message.get();
                ApiLogInfo logInfo = JSON.parseObject(String.valueOf(msg), ApiLogInfo.class);
                //true-第一次插入，false-第二次插入
                if(redisTemplate.opsForValue().setIfAbsent(logInfo.getTraceId(), logInfo, 5, TimeUnit.SECONDS)){
                    IcreditApiLogEntity apiLogEntity = new IcreditApiLogEntity();
                    BeanUtils.copyProperties(logInfo, apiLogEntity);
                    apiLogMapper.insert(apiLogEntity);
                }else{
                    if (!CallStatusEnum.CALL_ON.getCode().equals(logInfo.getCallStatus())){
                        IcreditApiLogEntity logApiLogEntity = apiLogMapper.findByTraceId(logInfo.getTraceId());
                        String logId = logApiLogEntity.getId();
                        Date createTime = logApiLogEntity.getCreateTime();
                        String createBy = logApiLogEntity.getCreateBy();
                        BeanUtils.copyProperties(logInfo, logApiLogEntity);
                        logApiLogEntity.setId(logId);
                        logApiLogEntity.setCreateTime(createTime);
                        logApiLogEntity.setCreateBy(createBy);
                        apiLogMapper.updateById(logApiLogEntity);
                    }
                    redisTemplate.delete(logInfo.getTraceId());
                }
                log.info("topic_test 消费了： Topic:" + topic + ",Message:" + msg);
            }
        } catch (BeansException e) {
            log.error("消费异常:Topic:{},Message:{}", topic, record.value());
        } finally {
            ack.acknowledge();
        }
    }

}
