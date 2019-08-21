# Kafka 项目模板


## 目录结构

```bash
src/
├── main
│   ├── java
│   │   └── com
│   │       └── jlpay
│   │           └── xxx
│   │               └── xxx
│   │                   ├── KafkaTemplateApplication.java           # 启动类
│   │                   ├── config        # 配置文件目录
│   │                   │   └── KafkaTemplateBeanPostProcessor.java # 配置Kafka生产者后置处理器
│   │                   └── service       # 业务处理目录
│   │                       └── KafkaConsumerListener.java          # Kafka消费者实现具体业务处理
│   └── resources
│       ├── application.yml              #配置文件
│       └── logback-spring.xml           #日志配置文件
└── test
    ├── java
    │   └── com
    │       └── jlpay
    │           └── xxx
    │               └── xxx
    │                   ├── config        # 配置文件目录
    │                   │   └── KafkaConfig.java                    # 配置消费者 为了Kafka消费特定偏移量消息
    │                   └── exemple       # 演示demo
    │                       ├── KafkaConsumerTest.java              # 演示Kafka消费特定偏移量消息
    │                       └── KafkaProducerTest.java              # 演示Kafka生产消息发送
    └── resources
        ├── application.yml              #配置文件
        └── logback-spring.xml           #日志配置文件
```

## 发送消息

- 使用Spring KafkaTemplate来发送消息

```java
        kafkaTemplate.send(topic, message);
```
- 监听处理发送成功/异常

在KafkaTemplateBeanPostProcessor中为kafkaTemplate配置发送结果监听器。

```java

/**
 * Kafka生产者后置处理器
 * 为KafkaTemplate增加发送成功失败监听处理
 * @author Shaofeng Li
 * @date 2019/8/12 15:03
 */

@Component
public class KafkaTemplateBeanPostProcessor implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(KafkaTemplateBeanPostProcessor.class);


    /**
     * 添加发送成功失败监听处理
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
                    log.error("发送消息失败！exception[{}] producerRecord[{}]  ", exception.getMessage(), producerRecord);
                    //  todo  建议 发送异常时保存未发送数据到数据库，另外一个任务处理发送失败的数据。

                }
            });
        }
        return bean;
    }

}
```

## 接收消息

- 使用Spring 提供的@KafkaListener注解接收消息

```java
 /**
     * 自动提交偏移量
     * @param record
     */
    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.topics}")
    public void listen(ConsumerRecord<String, String> record) {
        //todo 业务处理
        log.debug("接收消息topic[{}] partition[{}] offset[{}] value[{}]", record.topic(), record.partition(), record.offset(), record.value());

    }
   ```

如果要手动提交偏移量的话

```java
    /**
     * 手动提交偏移量模式
     * @param record
     * @param ack
     */
    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = "${spring.kafka.consumer.topics}")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        //todo 业务处理
        log.debug("接收消息topic[{}] partition[{}] offset[{}] value[{}]", record.topic(), record.partition(), record.offset(), record.value());
        ack.acknowledge();
    }
```

## 配置

### 高可靠（不丢消息，可能会重复消费）

#### 生产者

```bash
spring:
  kafka:
    producer:
      bootstrap-servers: 172.20.21.114:9091,172.20.21.114:9092,172.20.21.114:9093
      # 累积消息触发发送的字节数值大性能好 ， 实时性差，可靠性差. 
      # 和linger.ms属性只有有一个条件满足就会发送消息
      batch-size: 100
      # 消息的缓存容量  值不可低于batch-size.
      # 一定要比最大的消息大， 不然消息发布出去  消息长度超过此值会发送失败并爆出RecordTooLargeException
      buffer-memory: 102400
      # 指定消息key和消息体的编解码方式
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      # 重试次数
      retries: 0
      properties:
        # 累积消息触发发送的毫秒数(默认值0) 值大性能好实时性差. 
        # 和batch-size属性只有有一个条件满足就会发送消息
        linger.ms: 5
        #  指定了 生产者在 发送数据时 等待服务器返回响应的 时间，
        request.timeout.ms: 10000
        #  指定了 生产者在 获取元 数据（比如目标分区的首领是谁） 时等待服务器返回响应的时间。 如果等待响应超时， 那么生产者要么重试发送数据， 要么返回一个错误（抛出异常或执行回调）。
        metadata.fetch.timeout.ms: 10000
        #  指定了 broker等待同步副本返回返回消息确认的时间， 与asks的配置相匹配——如果在指定时间内没有收到同步副本的 确认， 那么broker就会返回一个错误。
        timeout.ms: 10000
        compression.type: gzip
      #  控制可靠性的关键  是否同步等待服务端应答数  0不等待1 learder应答  all  全部副本应答
      acks: all
```

#### 消费者

```bash
spring:
  kafka:
    # topic分片会均匀分配到消费者
    # 消费者大于分片数量，浪费，不会消费
    consumer:
      bootstrap-servers: 172.20.21.114:9091,172.20.21.114:9092,172.20.21.114:9093
      # 如果在消息处理完成前就提交了offset，那么就有可能造成数据的丢失。由于Kafkaconsumer默认是自动提交位移的，
      # 所以在后台提交位移前一定要保证消息被正常处理了，因此不建议采用很重的处理逻辑，如果处理耗时很长，
      # 如需手动提交则改为false ，对应ack-mode也得改为手动模式
      enable-auto-commit: true
      # earliest获取log头部的数据(可能有重复), latest获取log尾部的数据（可能会丢失消息）
      auto-offset-reset: earliest
      # 指定默认消费者groupid(会被KafkaListener注解的groupId属性覆盖) 同一个groupid只有一条消息只有一个消费者可以收到消息 ；
      # 不同同groupid只有一条消息每个group下面都会有一个消费者可以收到消息
      group-id: group-id-test
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      # 每次最多拉几条消息  值大性能好，
      max-poll-records: 1
      properties:
        max.poll.interval.ms: 600000  #消费者每次拉消息的间隔时间， 如果大于这个时间消费者分区会重新分配

    listener:
      # 指定listener容器中的线程数，用于提高并发量.  一个线程对应一个消费者， 消费者数量多余分区数量没用
      concurrency: 10
      # 提交offset模式，
      # 手动提交设置enable-auto-commit为 false (若为true则无法启动且抛出IllegalArgumentException), 并在KafkaListener注解的方法中引入org.springframework.kafka.support.Acknowledgment参数, 调用ack方法即可手动提交
      # MANUAL_IMMEDIATE会立刻提交, MANUAL是批量处理的
      ack-mode: record
```

### 高性能（可能丢消息，可能会重复消费）

## 生产者

```bash
spring:
  kafka:
    producer:
      bootstrap-servers: 172.20.21.114:9091,172.20.21.114:9092,172.20.21.114:9093
      # 累积消息触发发送的字节数值大性能好 ， 实时性差，可靠性差. 
      # 和linger.ms属性只有有一个条件满足就会发送消息
      batch-size: 10000
      # 消息的缓存容量  值不可低于batch-size.
      # 一定要比最大的消息大， 不然消息发布出去  消息长度超过此值会发送失败并爆出RecordTooLargeException
      buffer-memory: 102400
      # 指定消息key和消息体的编解码方式
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      # 重试次数
      retries: 0
      properties:
        # 累积消息触发发送的毫秒数(默认值0) 值大性能好实时性差. 
        # 和batch-size属性只有有一个条件满足就会发送消息
        linger.ms: 50
        #  指定了 生产者在 发送数据时 等待服务器返回响应的 时间，
        request.timeout.ms: 10000
        #  指定了 生产者在 获取元 数据（比如目标分区的首领是谁） 时等待服务器返回响应的 时间。 如果等待响应超时， 那么生产者要么重试发送数据， 要么返回一个错误（抛出异常或执行回调）。
        metadata.fetch.timeout.ms: 10000
        #  指定了 broker等待同步副本返回返回消息确认的时间， 与asks的配置相匹配—— 如果在指定时间内 没有收到同步副本的确认， 那么broker就会返回一个错误。
        timeout.ms: 10000
        compression.type: gzip
      #  控制可靠性的关键  是否同步等待服务端应答数  0不等待1 learder应答  all  全部副本应答
      acks: all
```

#### 消费者

```bash
spring:
  kafka:
    # topic分片会均匀分配到消费者
    # 消费者大于分片数量，浪费，不会消费
    consumer:
      bootstrap-servers: 172.20.21.114:9091,172.20.21.114:9092,172.20.21.114:9093
      # 如果在消息处理完成前就提交了offset，那么就有可能造成数据的丢失。由于Kafkaconsumer默认是自动提交位移的，
      # 所以在后台提交位移前一定要保证消息被正常处理了，因此不建议采用很重的处理逻辑，如果处理耗时很长，
      # 如需手动提交则改为false ，对应ack-mode也得改为手动模式
      enable-auto-commit: true
      # earliest获取log头部的数据(可能有重复), latest获取log尾部的数据（可能会丢失消息）
      auto-offset-reset: earliest
      # 指定默认消费者groupid(会被KafkaListener注解的groupId属性覆盖) 同一个groupid只有一条消息只有一个消费者可以收到消息 ；
      # 不同同groupid只有一条消息每个group下面都会有一个消费者可以收到消息
      group-id: group-id-test
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      # 每次最多拉几条消息  值大性能好，
      max-poll-records: 100
      properties:
        max.poll.interval.ms: 600000  #消费者每次拉消息的间隔时间， 如果大于这个时间消费者分区会重新分配

    listener:
      # 指定listener容器中的线程数，用于提高并发量. 一个线程对应一个消费者，消费者数量多余分区数量没用
      concurrency: 10
      # 提交offset模式，
      # 手动提交设置enable-auto-commit为 false (若为true则无法启动且抛出IllegalArgumentException), 并在KafkaListener注解的方法中引入org.springframework.kafka.support.Acknowledgment参数, 调用ack方法即可手动提交
      # MANUAL_IMMEDIATE会立刻提交, MANUAL是批量处理的
      ack-mode: record
```
