package com.zhbit.miaosha.service.impl;

import com.zhbit.miaosha.common.Constant;
import com.zhbit.miaosha.common.RestResponse;
import com.zhbit.miaosha.service.OmsOrderService;
import com.zhbit.miaosha.service.SeckillService;
import com.zhbit.miaosha.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 使用Redis事务在保证数据安全的情况下实现乐观锁机制（CAS算法）
 */
@Slf4j
@Service("seckillServiceRedisImpl")
@Primary
public class SeckillServiceRedisImpl implements SeckillService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OmsOrderService omsOrderService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void seckill(Long seckillId,Long memberId) {

        // 在redis提前预热好的秒杀库存进行减库存
        boolean flag = updateSeckillStock(seckillId);
        if (!flag) {    // 秒杀失败
            log.info("很遗憾!:{}没有抢到",memberId);
            return;
        }
        // 调用订单系统。进行减库存，下订单
        omsOrderService.orderGenerate(seckillId, memberId);

    }

    @Override
    public RestResponse seckillResult(Long seckillId, Long memberId) {
        // 从缓存中获取秒杀结果
        Object seckillOrder = redisUtil.get(Constant.SECKILL_ORDER + seckillId + "_" + memberId);
        if (seckillOrder == null) {
            return RestResponse.success("秒杀失败", null);
        }
        return RestResponse.success("秒杀成功", null);
    }

    /**
     * 减库存操作
     * @param seckillId 秒杀商品编号
     * @return
     */
    private boolean updateSeckillStock(Long seckillId) {
        // 创建redis 显式事务redisTemplate.execute(SessionCallback<Object>())
        Object execute = redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                @SuppressWarnings("unchecked")
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    while (true) {  //轮询（CAS机制）
                        // 监视库存，若在事务执行中库存被修改，则放弃一切操作
                        operations.watch((K) String.valueOf(seckillId));
                        try {
                            // 根据秒杀商品id-> 查询秒杀剩余库存
                            Integer stock = (Integer) operations.opsForValue().get(String.valueOf(seckillId));
                            if (stock == null || stock == 0) {  //库存不足，直接返回false
                                return false;
                            }
                            // 尝试减库存
                            // 开启事务
                            operations.multi();
                            // 发起减库存命令进入事务队列
                            operations.opsForValue().decrement((K) String.valueOf(seckillId));
                            // 提交事务
                            List<Object> results = operations.exec();
                            if (results == null || results.isEmpty()) { // 事务提交失败，重试
                                continue;
                            } else {
                                return true; // 扣减库存成功
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        return (boolean) execute;
    }

}
