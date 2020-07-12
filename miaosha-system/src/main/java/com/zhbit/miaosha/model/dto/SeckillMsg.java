package com.zhbit.miaosha.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 下秒杀订单请求的参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeckillMsg {
    /**
     * 秒杀商品编号
     */
    private Long seckillId;
    /**
     * 会员用户编号
     */
    private Long memberId;
}
