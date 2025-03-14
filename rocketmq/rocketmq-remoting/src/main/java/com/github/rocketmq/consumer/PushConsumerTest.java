package com.github.rocketmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * 简单消费者
 *
 * @author 杨中肖
 * @date 2025/03/07
 */
@Slf4j
public class PushConsumerTest {

    @Test
    public void testTag() throws MQClientException {
        DefaultMQPushConsumer consumer_1 = new DefaultMQPushConsumer("Consumer_1");
        consumer_1.setNamesrvAddr("10.10.1.5:9876");
        consumer_1.subscribe("TopicTest", "TagA");
        consumer_1.setConsumeMessageBatchMaxSize(5);
        consumer_1.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("consumer_1 消息数量：{}", msgs.size());
                for (MessageExt msg : msgs) {
                    String body = new String(msg.getBody());
                    log.info("consumer_1 消息内容：{}，tags: {}，id：{}", body, msg.getTags(), msg.getMsgId());
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer_1.start();

        DefaultMQPushConsumer consumer_2 = new DefaultMQPushConsumer("Consumer_1");
        consumer_2.setNamesrvAddr("10.10.1.5:9876");
        consumer_2.subscribe("TopicTest", "TagA");
        consumer_2.setConsumeMessageBatchMaxSize(5);
        consumer_2.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("consumer_2 消息数量：{}", msgs.size());
                for (MessageExt msg : msgs) {
                    String body = new String(msg.getBody());
                    log.info("consumer_2 消息内容：{}，tags: {}，id：{}", body, msg.getTags(), msg.getMsgId());
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer_2.start();
        LockSupport.park();
    }

    public static void main(String[] args) throws MQClientException {
        // 消费者1
        DefaultMQPushConsumer consumer_1 = new DefaultMQPushConsumer("Consumer_1");
        consumer_1.setNamesrvAddr("10.10.1.5:9876");
        consumer_1.subscribe("TopicTest", "*");
        // 设置当前消费者（consumer_1）的消费线程组每个线程最大消息数量（msgs.size() <= consumeMessageBatchMaxSize），默认值为1
        consumer_1.setConsumeMessageBatchMaxSize(5);
        consumer_1.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("consumer_1 消息数量：{}", msgs.size());
                for (MessageExt msg : msgs) {
                    String body = new String(msg.getBody());
                    log.info("consumer_1 消息内容：{}", body);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer_1.start();

        // 消费者2
        DefaultMQPushConsumer consumer_2 = new DefaultMQPushConsumer("Consumer_2");
        consumer_2.setNamesrvAddr("10.10.1.5:9876`");
        consumer_2.subscribe("TopicTest", "*");
        consumer_2.setConsumeMessageBatchMaxSize(3);
        consumer_2.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                log.info("consumer_2 消息数量：{}", msgs.size());
                for (MessageExt msg : msgs) {
                    String body = new String(msg.getBody());
                    log.info("consumer_2 消息内容：{}", body);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer_2.start();
    }
}
