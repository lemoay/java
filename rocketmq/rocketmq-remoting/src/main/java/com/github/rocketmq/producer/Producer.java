package com.github.rocketmq.producer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

@Slf4j
public class Producer {

    @SneakyThrows
    public static void main(String[] args) {
        log.info("程序开始执行");
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
            Message msg = new Message("TopicTest", "TagA", ("Hello RocketMQ " + i).getBytes());
            // 利用producer进行发送，并同步等待发送结果
            SendResult sendResult = producer.send(msg);
            log.info("发送结果：{}", sendResult);
        }
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("TopicTest", "TagB", ("Hello RocketMQ " + i).getBytes());
            // 利用producer进行发送，并同步等待发送结果
            SendResult sendResult = producer.send(msg);
            log.info("发送结果：{}", sendResult);
        }
        // 一旦producer不再使用，关闭producer
        producer.shutdown();
    }
}
