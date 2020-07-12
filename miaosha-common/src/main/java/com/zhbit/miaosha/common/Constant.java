package com.zhbit.miaosha.common;

/**
 * 全局常量
 * final 类型 且 构造器是私有的
 */
public final class Constant {
    //私有构造器，防止外部实例化
    private Constant() {
    }
    /** 启用 */
    public static final Integer ENABLE = 1;
    /** 禁用 */
    public static final Integer DISABLE = 0;

    /** Token请求头KEY */
    public static final String X_ACCESS_TOKEN = "X-Access-Token";
    /** Token令牌redis缓存KEY前缀 */
    public static final String PREFIX_USER_TOKEN = "prefix_user_token_";
    /** Redis 秒杀订单前缀 */
    public static final String SECKILL_ORDER = "seckill_order_";
    /** Redis 秒杀商品前缀 */
    public static final String SECKILL_GOODS = "seckill_goods_";
}
