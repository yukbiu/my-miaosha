package com.zhbit.miaosha;

import com.zhbit.miaosha.model.dto.SeckillMsg;
import com.zhbit.miaosha.rabbitmq.OrderSender;
import com.zhbit.miaosha.service.SeckillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

@SpringBootTest

public class MiaoShaApplicationTest {
    @Autowired
    private OrderSender orderSender;
    //    @Autowired
//    @Qualifier("seckillServiceRedisImpl")
    @Resource(name = "seckillServiceRedisImpl")
    SeckillService seckillService;
    @Autowired
    PasswordEncoder passwordEncoder;

    private static final int THREAD_NUM = 1000;
    // 线程发令枪 模拟并发场景
    private final CountDownLatch countDownLatch = new CountDownLatch(THREAD_NUM);
    //计时器
    private final CyclicBarrier barrier = new CyclicBarrier(THREAD_NUM + 1);

    private static SeckillMsg msg = new SeckillMsg(1278943001673990144L,1277933522262163456L);
    @Test

    void contextLoads() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < THREAD_NUM; i++) {
            new Thread(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                orderSender.sendSeckillMsg(msg);
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
            countDownLatch.countDown();
        }
        try {
            barrier.await();    // 主线程等待并发线程完成
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();  // 并发线程执行完毕后记录时间

//        System.out.println("剩余库存：" + seckillDao.selectStockById(1278943001673990144L).getSeckillStock());
        System.out.println("耗时:" + (end - start));
    }
}
