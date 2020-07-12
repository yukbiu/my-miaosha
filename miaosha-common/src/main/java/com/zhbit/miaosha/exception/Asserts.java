package com.zhbit.miaosha.exception;

import com.zhbit.miaosha.common.RestHttpStatus;

/**
 * 断言类处理类，用于抛出各种api异常
 */
public class Asserts {
    /**
     * 自定义业务异常
     * @param status 封装状态码 + 异常信息
     * @return
     */
    public static void failed(RestHttpStatus status) {
        throw new BusinessException(status);
    }

    /**
     * 自定义业务异常
     * @param message 异常信息
     * @return
     */
    public static void failed(String message) {
        throw new BusinessException(RestHttpStatus.FAILED.getCode(),message);
    }
}
