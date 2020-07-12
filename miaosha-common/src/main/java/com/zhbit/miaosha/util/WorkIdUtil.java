package com.zhbit.miaosha.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * 数据库主键生成工具
 */
public class WorkIdUtil {
    private static final Snowflake snowflake = IdUtil.createSnowflake(0L, 0L);

    /**
     * 雪花算法生成全局唯一id策略
     * @return
     */
    public static Long workId() {
        return snowflake.nextId();
    }
}
