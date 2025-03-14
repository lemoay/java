package com.github.rocketmq.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 简单消费者
 *
 * @author 杨中肖
 * @date 2025/03/07
 */
@Slf4j
public class PushConsumerTest1 {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer_1 = new DefaultMQPushConsumer("Consumer_1");
        consumer_1.setNamesrvAddr("192.168.192.96:9876");
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

        DefaultMQPushConsumer consumer_2 = new DefaultMQPushConsumer("Consumer_3");
        consumer_2.setNamesrvAddr("192.168.192.96:9876");
        consumer_2.subscribe("TopicTest", "TagB");
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
    }
}
