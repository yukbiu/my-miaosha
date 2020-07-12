package com.zhbit.miaosha.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 */
@Data
public class OmsOrder implements Serializable {
    private static final long serialVersionUID = 7528477991826770278L;
    /**
     * 订单id
     */
    private Long id;
    /**
     * 会员id
     */
    private Long memberId;
    /**
     * 收货地址
     */
    private String receiverAddress;
    /**
     * 收货人电话
     */
    private String receiverPhone;
    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     */
    private Integer status;
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    /**
     * 订单类型：1->普通订单；2->秒杀订单
     */
    private Integer orderType;
    /**
     * 订单生成时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT")
    private Date updateTime;
}
