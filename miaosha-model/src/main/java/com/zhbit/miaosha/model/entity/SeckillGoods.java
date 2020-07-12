package com.zhbit.miaosha.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品
 */
@Data
public class SeckillGoods implements Serializable {
    private static final long serialVersionUID = -4304048517974200752L;
    /**
     * 秒杀id
     */
    private Long seckillId;
    /**
     * 秒杀商品
     */
    private GmsGoods goods;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀库存
     */
    private Integer seckillStock;
    /**
     * 秒杀开始时间
     */
    private Date startTime;
    /**
     * 秒杀结束时间
     */
    private Date endTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
