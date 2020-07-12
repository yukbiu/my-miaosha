package com.zhbit.miaosha.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品<SKU>
 */
@Data
public class GmsGoods implements Serializable {
    private static final long serialVersionUID = -5567201653261175311L;
    /**
     * 商品id
     */
    private Long id;
    /**
     * 商品名称
     */
    private String name;
    /**
     * 副标题
     */
    private String subtitle;
    /**
     * 商品详情描述
     */
    private String description;
    /**
     * 商品图片
     */
    private String images;
    /**
     * 商品单价
     */
    private BigDecimal price;
    /**
     * 库存数量
     */
    private Integer stock;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
