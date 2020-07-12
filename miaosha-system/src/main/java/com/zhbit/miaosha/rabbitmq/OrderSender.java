package com.zhbit.miaosha.rabbitmq;

import com.zhbit.miaosha.model.dto.SeckillMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Slf4j
@Component
public class OrderSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀请求（削峰）
     * @param seckillMsg 秒杀业务参数 seckillId->秒杀商品id；memberId->会员用户id
     * @return
     */
    public void sendSeckillMsg(SeckillMsg seckillMsg) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_ORDER_DIRECT,
                RabbitConfig.QUEUE_ORDER_SECKILL, seckillMsg);
//        RestResponse result = (RestResponse) rabbitTemplate.convertSendAndReceive(QueueEnum.QUEUE_ORDER.getExchange(),
//                QueueEnum.QUEUE_ORDER.getRoutingKey(), seckillMsg);

//        assert result != null;
//        log.info(seckillMsg.getMemberId()+"：" + result.getMessage());
//        return result;
    }

//    /**
//     * 发送秒杀订单生成消息
//     */
//    public void sendGenerateOrderMsg(SeckillMsg seckillMsg) {
//
//        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_ORDER_DIRECT,
//                RabbitmqConfig.QUEUE_ORDER_GENERATE,seckillMsg);
//
//    }

    /**
     * 延迟队列（死信队列）
     * @param orderId 秒杀订单id
     * @param delayTimes 消息延迟毫秒值
     */
    public void ttlOrderCancelMsg(Long orderId ,final long delayTimes) {
        // 给延迟队列发送消息
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_TTL_ORDER_DIRECT,
                RabbitConfig.QUEUE_TTL_ORDER,orderId,message -> {
                    // 给消息设置延迟时间，延迟时间一到立马被消费
                    message.getMessageProperties().setExpiration(String.valueOf(delayTimes));
                    return message;
                });
    }
}
