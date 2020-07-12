package com.zhbit.miaosha;

import com.zhbit.miaosha.rabbitmq.RabbitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MQTest {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    AmqpAdmin amqpAdmin;

    @Test
    void test() {
        QueueInformation queueInfo = amqpAdmin.getQueueInfo(RabbitConfig.QUEUE_ORDER_SECKILL);
        assert queueInfo != null;
        System.out.println(queueInfo.toString());
    }
}
