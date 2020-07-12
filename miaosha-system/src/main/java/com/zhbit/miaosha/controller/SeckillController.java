package com.zhbit.miaosha.controller;

import com.zhbit.miaosha.common.RestResponse;
import com.zhbit.miaosha.model.dto.SeckillMsg;
import com.zhbit.miaosha.rabbitmq.OrderSender;
import com.zhbit.miaosha.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 秒杀活动 SeckillController
 */
@RestController
public class SeckillController {
    @Autowired
    private OrderSender orderSender;
    @Autowired
    private SeckillService seckillService;
    @PostMapping("/seckill/{seckillId}")
    public RestResponse actionSeckill(@PathVariable("seckillId") Long seckillId,
                                      @RequestParam("memberId") Long memberId) {
        // 秒杀请求消息封装类
        SeckillMsg msg = new SeckillMsg(seckillId, memberId);

        orderSender.sendSeckillMsg(msg);

        return RestResponse.success("秒杀中",null);
    }

    @PostMapping("/seckill/result/{seckillId}")
    public RestResponse SeckillResult(@PathVariable("seckillId") Long seckillId,
                                      @RequestParam("memberId") Long memberId) {

        return seckillService.seckillResult(seckillId, memberId);
    }
}
