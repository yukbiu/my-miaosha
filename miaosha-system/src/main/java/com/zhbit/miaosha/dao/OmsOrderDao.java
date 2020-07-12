package com.zhbit.miaosha.dao;

import com.zhbit.miaosha.model.entity.OmsOrder;
import com.zhbit.miaosha.model.entity.OmsOrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OmsOrderDao {
    /**
     * 添加订单
     */
    void addSeckillOrder(OmsOrder order);

    /**
     * 添加订单行项目
     */
    void addOrderItems(OmsOrderItem orderItem);

    /**
     * 获取订单信息
     */
    OmsOrder getOrderById(Long orderId);

    /**
     * 根据订单id查询所有订单商品
     */
    List<OmsOrderItem> getOrderItemsByOrderId(Long orderId);
    /**
     * 更新订单信息
     */
    void updateOrder(OmsOrder order);

    /**
     * 取消超时订单-> 修改订单状态
     */
    void cancelOrder(@Param("orderId") Long orderId, @Param("status") Integer status);
}
