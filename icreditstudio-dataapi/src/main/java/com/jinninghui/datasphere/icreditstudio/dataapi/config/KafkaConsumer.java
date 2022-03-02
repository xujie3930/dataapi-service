package com.jinninghui.datasphere.icreditstudio.dataapi.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;
/**
 * @author xujie
 * @description kafka消费者
 * @create 2022-03-02 16:57
 **/
@Component
@Slf4j
public class KafkaConsumer {

    //自定义topic
    public static final String TOPIC_TEST = "topic.test";

    @KafkaListener(topics = KafkaProducer.TOPIC)
    public void topic_test(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            log.info("topic_test 消费了： Topic:" + topic + ",Message:" + msg);
            ack.acknowledge();
        }
    }

    @KafkaListener(topics = KafkaProducer.TOPIC)
    public void topic_test1(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        Optional message = Optional.ofNullable(record.value());
        if (message.isPresent()) {
            Object msg = message.get();
            log.info("topic_test1 消费了： Topic:" + topic + ",Message:" + msg);
            ack.acknowledge();
        }
    }
}
