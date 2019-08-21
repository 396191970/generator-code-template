package com.jlpay.xxx.xxx.exemple;

import com.jlpay.xxx.xxx.KafkaTemplateApplication;
import com.jlpay.xxx.xxx.config.KafkaConfig;
import com.jlpay.xxx.xxx.service.KafkaConsumerListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试自动提交模式，如果发生异常偏移量会提交。消费异常消息会在setErrorHandler 监听到，可做异常记录。以便再次消费
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaConsumerAutoRecordTest.class)
@SpringBootApplication
@ComponentScan(basePackages = {"com.jlpay.xxx.xxx"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {KafkaConfig.class, KafkaConsumerListener.class, KafkaTemplateApplication.class,KafkaConsumerManualTest.class}))
public class KafkaConsumerAutoRecordTest {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerAutoRecordTest.class);


    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.topics}")
    public void listen(ConsumerRecord<String, String> record) {

        log.debug("接收消息topic[{}] partition[{}] offset[{}] value[{}]", record.topic(), record.partition(), record.offset(), record.value());
        //测试异常情况是否会重复消费失败消息
        throw new RuntimeException("测试异常情况是否会重复消费失败消息");
    }

    @Test
    public void listen() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}