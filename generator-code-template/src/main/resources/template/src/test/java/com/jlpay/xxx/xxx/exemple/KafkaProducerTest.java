package com.jlpay.xxx.xxx.exemple;

import com.jlpay.xxx.xxx.KafkaTemplateApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * 演示Kafka生产消息发送
 * @author Shaofeng Li
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = KafkaTemplateApplication.class)
public class KafkaProducerTest {


    @Autowired
    private KafkaTemplate kafkaTemplate;

    public static final String TOPIC = "test-topic-template";


    /**
     * 供测试。
     * todo 根据业务场景直接使用 kafkaTemplate.send(topic, message);
     * @return
     */
    @Test
    public void sendMessage() {
        kafkaTemplate.send(TOPIC, "message");
    }


    @Test
    public void sendMessage10() {
        int i = 10;
        while(i-- >0)
        {
            sendMessage();
        }
    }

}