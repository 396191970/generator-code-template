package com.jlpay.xxx.xxx.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 接收kafka消息，实现业务处理流程。
 * @author Shaofeng Li
 * @date 2019/7/17 14:47
 */
@Component
public class KafkaConsumerListener {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerListener.class);

    /**
     * 自动提交偏移量
     * @param record
     */
    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.topics}")
    public void listen(ConsumerRecord<String, String> record) {
        //todo 业务处理
        log.debug("接收消息topic[{}] partition[{}] offset[{}] value[{}]", record.topic(), record.partition(), record.offset(), record.value());
    }


    /**
     * 手动提交偏移量模式
     * @param record
     * @param ack
     */
//    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.topics}")
//    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
//        //todo 业务处理
//        log.debug("接收消息topic[{}] partition[{}] offset[{}] value[{}]", record.topic(), record.partition(), record.offset(), record.value());
//        ack.acknowledge();
//    }


}
