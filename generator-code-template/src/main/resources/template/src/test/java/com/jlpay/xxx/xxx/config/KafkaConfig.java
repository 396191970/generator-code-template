package com.jlpay.xxx.xxx.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 配置消费者 为了Kafka消费特定偏移量消息
 * todo 不是每个项目都需要这种处理方式。这里只是为了演示如何使用。不需要的可以删除
 * @author Shaofeng Li
 * @date 2019/8/12 11:46
 */
@Configuration
public class KafkaConfig {

    @Autowired
    private KafkaProperties kafkaProperties;
    /**
     * 用工厂类生产消费者
     * @return
     */
    @Bean
    public Consumer consumer() {
        Map<String, Object> stringObjectMap = kafkaProperties.buildConsumerProperties();
        stringObjectMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,"1");
        Consumer consumer =  new KafkaConsumer<>(stringObjectMap);

        return consumer;
    }



}
