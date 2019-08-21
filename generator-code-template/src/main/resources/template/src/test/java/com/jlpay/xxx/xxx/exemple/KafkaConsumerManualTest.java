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
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试手动提交模式，只有手动调用ack.acknowledge()才会提交偏移量。
 * 如果前面的消息没有应答，后面的消息提交了偏移量后，会被自动处理默认为前面的全部消息都有处理成功。
 * 消费异常消息会在setErrorHandler 监听到，可做异常记录。
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaConsumerManualTest.class)
@SpringBootApplication
@ComponentScan(basePackages = {"com.jlpay.xxx.xxx"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {KafkaConfig.class, KafkaConsumerListener.class, KafkaTemplateApplication.class, KafkaConsumerAutoRecordTest.class}))
@ActiveProfiles("manual")
public class KafkaConsumerManualTest {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerManualTest.class);


    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.topics}")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {

        log.debug("接收消息topic[{}] partition[{}] offset[{}] value[{}]", record.topic(), record.partition(), record.offset(), record.value());
        if (record.partition() == 0 && record.offset() == 1)
            ack.acknowledge(); //测试异常情况是否会重复消费失败消息
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