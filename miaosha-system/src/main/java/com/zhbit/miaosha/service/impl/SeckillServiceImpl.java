package com.zhbit.miaosha.service.impl;

import com.zhbit.miaosha.common.RestResponse;
import com.zhbit.miaosha.dao.SeckillDao;
import com.zhbit.miaosha.model.entity.SeckillGoods;
import com.zhbit.miaosha.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 使用悲观锁解决高并发下的线程安全问题
 * 缺点：
 *      效率低下（由并行变成串行）
 */
@Service("seckillServiceImpl")
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private SeckillDao seckillDao;

    @Override
    public void seckill(Long seckillId, Long memberId) {

        // 减库存
        if (!updateSeckillStock(seckillId)) {
            System.out.println("很遗憾!没有抢到;别灰心,下次还有机会(●'◡'●)");
            return;
        }
//        // 添加秒杀订单和秒杀订单商品行项目
//        order.setId(WorkIdUtil.workId());
//        order.setOrderType(2);
//        order.setCreateTime(new Date());
//        seckillDao.addSeckillOrder(order);
//        orderItem.setId(WorkIdUtil.workId());
//        orderItem.setOrderId(order.getId());
//        orderItem.setCreateTime(new Date());
//        seckillDao.addOrderItems(orderItem);
    }

    @Override
    public RestResponse seckillResult(Long seckillId, Long memberId) {
        return null;
    }

    private synchronized boolean updateSeckillStock(Long seckillId) {

        // 查询秒杀库存
        SeckillGoods seckillGoods = seckillDao.selectStockById(seckillId);
        // 获取当前秒杀商品的剩余库存量
        Integer stock = seckillGoods.getSeckillStock();
        if (stock == 0) {   // 如果库存为零，直接返回秒杀失败
            return false;
        }
        return seckillDao.freezeStock(seckillId, 1) > 0;
    }
}
