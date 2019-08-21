package com.jlpay.xxx.xxx.config;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

/**
 * Kafka后置处理器 为KafkaTemplate增加发送成功失败监听处理
 *
 * @author Shaofeng Li
 * @date 2019/8/12 15:03
 */

@Component
public class KafkaTemplateBeanPostProcessor implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(KafkaTemplateBeanPostProcessor.class);

    /**
     * 添加发送成功失败监听处理
     * 添加消费失败监听处理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof KafkaTemplate) {
            KafkaTemplate kafkaTemplate = (KafkaTemplate) bean;
            kafkaTemplate.setProducerListener(new ProducerListener<String, String>() {
                @Override
                public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
                    //todo 发送消息成功处理
                }

                @Override
                public void onError(ProducerRecord<String, String> producerRecord, Exception exception) {
                    log.error("发送消息失败！topic[{}] partition[{}] offset[{}] value[{}]", producerRecord.topic(), producerRecord.partition(), producerRecord.value(),exception);
                    //  todo  建议 发送异常时保存未发送数据到数据库，另外一个任务处理发送失败的数据。

                }
            });
        } else if (bean instanceof ConcurrentKafkaListenerContainerFactory) {
            ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory = (ConcurrentKafkaListenerContainerFactory) bean;
            concurrentKafkaListenerContainerFactory.getContainerProperties().setErrorHandler((thrownException, record) -> {
                log.error("消费消息失败：topic[{}] partition[{}] offset[{}] value[{}]", record.topic(), record.partition(), record.offset(), record.value(),thrownException);
                //  todo  建议 消费异常时保存数据到数据库，另外一个任务处理消费异常的数据，可根据topic partition offset 重新消费。
            });
        }

        return bean;
    }



}
