package com.zhbit.miaosha.service.impl;

import com.zhbit.miaosha.common.Constant;
import com.zhbit.miaosha.dao.OmsOrderDao;
import com.zhbit.miaosha.dao.SeckillDao;
import com.zhbit.miaosha.model.entity.OmsOrder;
import com.zhbit.miaosha.model.entity.OmsOrderItem;
import com.zhbit.miaosha.model.entity.SeckillGoods;
import com.zhbit.miaosha.model.vo.OrderDetailsVo;
import com.zhbit.miaosha.rabbitmq.OrderSender;
import com.zhbit.miaosha.service.OmsOrderService;
import com.zhbit.miaosha.util.RedisUtil;
import com.zhbit.miaosha.util.WorkIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderDao omsOrderDao;
    @Autowired
    private SeckillDao seckillDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private OrderSender orderSender;
    @Override
    public void orderGenerate(Long seckillId,Long memberId) {
        // 查询秒杀库存
        SeckillGoods seckillGoods = seckillDao.selectStockById(seckillId);
        // 获取当前秒杀商品的剩余库存量
        Integer stock = seckillGoods.getSeckillStock();
        if (stock == 0) {    // 秒杀库存不足
            log.info("库存不足！");
            return;
        }
        // 冻结库存（预扣减库存）
        Integer integer = seckillDao.freezeStock(seckillId, 1);
        if (!(integer > 0)) {    // 库存冻结失败
            log.info("库存冻结失败！");
            return;
        }
        // 生成秒杀订单
        OrderDetailsVo orderDetails;    // 订单详情信息
        OmsOrder order = new OmsOrder();    // 订单信息
        order.setId(WorkIdUtil.workId());
        order.setMemberId(memberId);
        order.setOrderType(2);
        order.setStatus(0);
        order.setCreateTime(new Date());
        OmsOrderItem orderItem = new OmsOrderItem();    // 订单行项目信息
        orderItem.setId(WorkIdUtil.workId());
        orderItem.setOrderId(order.getId());
        orderItem.setGoodsId(seckillId);
        orderItem.setGoodsName(seckillGoods.getGoods().getName());
        orderItem.setGoodsPrice(seckillGoods.getSeckillPrice());
        orderItem.setQuantity(1);
        orderItem.setCreateTime(new Date());
        // 总金额 = (商品单价 × 购买数量) => BigDecimal.multiply()
        order.setTotalAmount(orderItem.getGoodsPrice().multiply(new BigDecimal(orderItem.getQuantity())));

        // 添加订单
        omsOrderDao.addSeckillOrder(order);
        // 添加订单行项目
        omsOrderDao.addOrderItems(orderItem);
        log.info("创建秒杀订单成功:{}",order.getId());
        // 发送延迟队列，超时订单自动取消 => Test:一分钟自动取消
        orderSender.ttlOrderCancelMsg(order.getId(), 60 * 1000);
        // 订单详情存入缓存中，秒杀成功
        orderDetails = new OrderDetailsVo(order, Collections.singletonList(orderItem));
        redisUtil.set(Constant.SECKILL_ORDER + seckillId + "_" + memberId, orderDetails, 600);
    }

    @Override
    public void orderCancel(Long orderId) {
        // 查询订单状态是否为未支付
        OmsOrder order = omsOrderDao.getOrderById(orderId);
        if (order == null || order.getStatus() != 0) {   // 订单已支付
            return;
        }
        // 取消超时订单->设置订单为交易取消状态 4 （关闭）
        omsOrderDao.cancelOrder(orderId,4);
        // 获取所有订单商品
        List<OmsOrderItem> orderItems = omsOrderDao.getOrderItemsByOrderId(orderId);
        // 解除订单商品的库存锁定
        seckillDao.unfreezeStock(orderItems);
    }
}
