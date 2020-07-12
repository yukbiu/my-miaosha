package com.zhbit.miaosha.handler;

import com.zhbit.miaosha.common.RestResponse;
import com.zhbit.miaosha.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局统一异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理所有不可知的异常
     */
    @ExceptionHandler(Exception.class)
    public <T> RestResponse<T> exceptionHandler(Exception e) {
        log.error("\n==========系统发生错误==========\n" +
                "引起异常的原因>>>>>>>>>>" + e +
                "\n异常信息>>>>>>>>>>" + e.getMessage() +
                "\n异常跟踪栈>>>>>>>>>>{}", (Object[]) e.getStackTrace());
        return RestResponse.error();
    }
    /**
     * 拦截捕捉自定义异常
     * 自定义抛出异常。统一的在这里捕获返回JSON格式的友好提示。
     */
    @ExceptionHandler(BusinessException.class)
    public <T> RestResponse<T> businessExceptionHandler(BusinessException e) {
        return RestResponse.failed(e.getMessage());
    }
}
