package com.jinninghui.datasphere.icreditstudio.dataapi.kafaka;

import com.alibaba.fastjson.JSON;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.ApiLogInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiLogEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.kafka.KafkaProducer;
import com.jinninghui.datasphere.icreditstudio.dataapi.mapper.IcreditApiLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;
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

    @KafkaListener(groupId = "test",topics = KafkaProducer.TOPIC)
    public void topic_test(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            String o = JSON.toJSONString(msg);
            ApiLogInfo logInfo = JSON.parseObject(o, ApiLogInfo.class) ;
            IcreditApiLogEntity logApiLogEntity = apiLogMapper.findByTraceId(logInfo.getTraceId());
            if(null != logApiLogEntity){
                String logId = logApiLogEntity.getId();
                Date createTime = logApiLogEntity.getCreateTime();
                String createBy = logApiLogEntity.getCreateBy();
                BeanUtils.copyProperties(logInfo, logApiLogEntity);
                logApiLogEntity.setId(logId);
                logApiLogEntity.setCreateTime(createTime);
                logApiLogEntity.setCreateBy(createBy);
                apiLogMapper.updateById(logApiLogEntity);
            }else{
                IcreditApiLogEntity apiLogEntity = new IcreditApiLogEntity();
                BeanUtils.copyProperties(logInfo, apiLogEntity);
                apiLogMapper.insert(apiLogEntity);
            }
            log.info("topic_test 消费了： Topic:" + topic + ",Message:" + msg);
            ack.acknowledge();
        }
    }
}
