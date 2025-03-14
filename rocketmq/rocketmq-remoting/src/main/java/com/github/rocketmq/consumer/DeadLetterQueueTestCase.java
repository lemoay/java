package com.github.rocketmq.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.LockSupport;

/**
 * 测试用例：验证RocketMQ死信队列（Dead Letter Queue, DLQ）功能
 *
 * <p>本测试案例旨在演示并验证RocketMQ中死信队列机制的工作原理。通常情况下，顺序消息的最大重试次数被设置为Integer.MAX_VALUE，
 * 意味着除非显式调整此参数，否则消息即使在消费失败后也不会自动迁移到DLQ。
 *
 * <p>通过该示例，我们首先配置一个生产者来向特定主题发送一系列（共计10条）的消息。接着，构建一个消费者实例，模拟一种场景，在这种场景下，
 * 无论接收到什么消息，消费者都将报告处理失败（通过返回RECONSUME_LATER状态）。为了触发死信队列机制，我们将消费者的最大重试尝试次数设为2。
 * 这意味着每条消息在经历两次重试失败之后，将不再继续尝试而是直接被路由至对应的DLQ中。根据RocketMQ的命名规则，这些无法成功处理的消息最终会被存储于名为
 * "%DLQ%DeadLetter_Consumer_1"的主题内。
 *
 * <p>注意: 完成所有测试步骤后，请记得手动关闭生产者与消费者的连接，并检查目标DLQ以确认消息是否如预期般正确地转移至此。
 *
 * @author 杨中肖
 * @date 2025/03/14
 */

@Slf4j
public class DeadLetterQueueTestCase {
    @Test
    @SneakyThrows
    public void createDeadLetter() {
        // 初始化一个producer并设置Producer group name
        DefaultMQProducer producer = new DefaultMQProducer("G1");
        // 设置NameServer地址
        producer.setNamesrvAddr("172.18.212.169:9876");
        producer.setSendMsgTimeout(1000000);
        producer.setMqClientApiTimeout(10 * 1000);
        // 启动producer
        producer.start();
        log.info("producer启动成功");
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("DeadLetterTopic", null, ("Hello RocketMQ " + i).getBytes());
            // 利用producer进行发送，并同步等待发送结果
            SendResult sendResult = producer.send(msg);
            log.info("发送结果：{}", sendResult);
        }

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("DeadLetter_Consumer_1");
        consumer.setNamesrvAddr("172.18.212.169:9876");
        consumer.subscribe("DeadLetterTopic", "*");
        // 设置重新消费次数为2，超过2次后将消息发送到死信队列（为了快速测试）
        consumer.setMaxReconsumeTimes(2);
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        });
        consumer.start();
        LockSupport.park();
    }
}
