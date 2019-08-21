package com.jlpay.xxx.xxx.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Kafka后置处理器 为方便测试，每次都从主题的最开始消费
 *
 * @author Shaofeng Li
 * @date 2019/8/12 15:03
 */

@Component
public class KafkaTemplateBeanPostProcessorTest implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(KafkaTemplateBeanPostProcessorTest.class);

    /**
     * 为方便测试，每次都从主题的最开始消费
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof ConcurrentKafkaListenerContainerFactory) {
            ConcurrentKafkaListenerContainerFactory concurrentKafkaListenerContainerFactory = (ConcurrentKafkaListenerContainerFactory) bean;

            concurrentKafkaListenerContainerFactory.getContainerProperties().setConsumerRebalanceListener(
                    new ConsumerAwareRebalanceListener() {
                        @Override
                        public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                            consumer.seekToBeginning(partitions);
                            log.info("重新消费从第一条记录");
                        }
                    }

            );
        }

        return bean;
    }


}
