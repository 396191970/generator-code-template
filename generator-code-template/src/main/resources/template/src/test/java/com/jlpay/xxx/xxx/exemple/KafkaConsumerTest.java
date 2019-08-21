package com.jlpay.xxx.xxx.exemple;

import com.jlpay.xxx.xxx.KafkaTemplateApplication;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;


/**
 * 演示Kafka消费特定偏移量消息
 *
 * @author Shaofeng Li
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = KafkaTemplateApplication.class)
public class KafkaConsumerTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerTest.class);
    public static final String TOPIC = "test-topic-template";
    public static final long TIMEOUT = 100L;
    public static final int PARTITION = 0;
    public static final int OFFSET = 0;

    @Autowired
    private Consumer consumer;


    /**
     * 演示 获取特定主题 分区 偏移量的消息
     *
     * @return
     */
    @Test
    public void fetch() {
        fetch1();
        fetch1();
    }

    public String fetch1() {

        String ret = "";

        //分配主题 分区
        ArrayList<TopicPartition> topicPartitions = new ArrayList<>();
        TopicPartition topicPartition = new TopicPartition(TOPIC, PARTITION);
        topicPartitions.add(topicPartition);
        consumer.assign(topicPartitions);

        //指定 偏移量
        consumer.seek(topicPartition, OFFSET);

        //获取消息
        ConsumerRecords records = consumer.poll(TIMEOUT);
        log.info("消费到数据records：{}", records);

        //遍历消息处理 todo
        for (Object o : records) {
            ConsumerRecord next = (ConsumerRecord) o;
            ret = next.toString();
            log.info("消费到数据：{}", ret);
        }
        //提交偏移量
        consumer.commitAsync();

        return ret;
    }


}