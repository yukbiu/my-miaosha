package com.zhbit.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列配置
 */
@EnableRabbit
@Configuration
public class RabbitConfig {
    /** 秒杀请求限流 */
    public static final int ACCESS_LIMIT = 100;

    // =================Exchange================
    /** 订单处理交换器 */
    public static final String EXCHANGE_ORDER_DIRECT = "miaosha.order.direct";
    /** 订单延迟处理交换器 */
    public static final String EXCHANGE_TTL_ORDER_DIRECT = "miaosha.order.direct.ttl";

    // ==================Queue==================
    /** 秒杀请求队列 */
    public static final String QUEUE_ORDER_SECKILL = "miaosha.order.seckill";
    /** 订单超时信息死信队列 */
    public static final String QUEUE_TTL_ORDER = "miaosha.order.ttl";
    /** 秒杀订单生成队列 */
    public static final String QUEUE_ORDER_GENERATE = "miaosha.order.generate";
    /** 超时订单取消队列 */
    public static final String QUEUE_ORDER_CANCEL = "miaosha.order.cancel";

    /**
     * 配置消息json序列化机制
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 订单消息实际消费队列所绑定的交换机
     */
    @Bean
    DirectExchange orderDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(EXCHANGE_ORDER_DIRECT)
                .durable(true)
                .build();
    }

    /**
     * 订单延迟队列所绑定的交换机
     */
    @Bean
    DirectExchange orderTtlDirect() {
        return (DirectExchange) ExchangeBuilder
                .directExchange(EXCHANGE_TTL_ORDER_DIRECT)
                .durable(true)
                .build();
    }
    // ----------------------------------------------------------------------------
    /**
     * 秒杀请求队列
     */
    @Bean
    public Queue orderSeckillQueue() {
        return QueueBuilder
                .durable(QUEUE_ORDER_SECKILL)
                .maxLength(ACCESS_LIMIT)    // 削峰限流
                .overflow(QueueBuilder.Overflow.rejectPublish)  // 拒绝策略：超过队列容量大小将丢弃消息
                .build();
    }

    /**
     * 秒杀订单生成队列
     */
    @Bean
    public Queue orderGenerateQueue() {
        return QueueBuilder
                .durable(QUEUE_ORDER_GENERATE)
                .build();
    }

    /**
     * 超时订单取消队列
     */
    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder
                .durable(QUEUE_ORDER_CANCEL)
                .build();
    }

    /**
     * 订单延迟队列（死信队列）
     */
    @Bean
    public Queue orderTtlQueue() {
        return QueueBuilder
                .durable(QUEUE_TTL_ORDER)
                .deadLetterExchange(EXCHANGE_ORDER_DIRECT)//到期后转发的交换机
                .deadLetterRoutingKey(QUEUE_ORDER_CANCEL)//到期后转发的路由键
                .build();
    }
    // -----------------------------------------------------------------------------
    /**
     * 将订单队列绑定到交换机
     */
    @Bean
    Binding orderBinding01(DirectExchange orderDirect,Queue orderSeckillQueue){
        return BindingBuilder
                .bind(orderSeckillQueue)
                .to(orderDirect)
                .with(QUEUE_ORDER_SECKILL);
    }
    @Bean
    Binding orderBinding02(DirectExchange orderDirect,Queue orderGenerateQueue){
        return BindingBuilder
                .bind(orderGenerateQueue)
                .to(orderDirect)
                .with(QUEUE_ORDER_GENERATE);
    }
    @Bean
    Binding orderBinding03(DirectExchange orderDirect,Queue orderCancelQueue){
        return BindingBuilder
                .bind(orderCancelQueue)
                .to(orderDirect)
                .with(QUEUE_ORDER_CANCEL);
    }
    /**
     * 将订单延迟队列绑定到交换机
     */
    @Bean
    Binding orderTtlBinding(DirectExchange orderTtlDirect,Queue orderTtlQueue){
        return BindingBuilder
                .bind(orderTtlQueue)
                .to(orderTtlDirect)
                .with(QUEUE_TTL_ORDER);
    }
}
