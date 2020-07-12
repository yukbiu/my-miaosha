package com.zhbit.miaosha.dao;

import com.zhbit.miaosha.model.entity.OmsOrderItem;
import com.zhbit.miaosha.model.entity.SeckillGoods;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 库存相关操作
 */
@Repository
public interface SeckillDao {
    /**
     * 查询秒杀库存
     */
    SeckillGoods selectStockById(Long seckillId);

    /**
     * 冻结库存（实际库存 - 商品下单数量）
     */
    Integer freezeStock(@Param("seckillId") Long seckillId,
                        @Param("quantity") Integer quantity);

    /**
     * 解除取消订单的库存锁定
     */
    Integer unfreezeStock(@Param("itemList") List<OmsOrderItem> orderItems);
}
