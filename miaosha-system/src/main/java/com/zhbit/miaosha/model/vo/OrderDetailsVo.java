package com.zhbit.miaosha.model.vo;

import com.zhbit.miaosha.model.entity.OmsOrder;
import com.zhbit.miaosha.model.entity.OmsOrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 返回给前端的订单详情
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsVo implements Serializable {
    private static final long serialVersionUID = -9147920136960154213L;
    /**
     * 订单详情
     */
    private OmsOrder order;
    /**
     * 订单所包含的商品行项目集
     */
    private List<OmsOrderItem> items;
}
