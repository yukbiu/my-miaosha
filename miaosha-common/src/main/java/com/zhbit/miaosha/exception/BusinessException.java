package com.zhbit.miaosha.exception;

import com.zhbit.miaosha.common.RestHttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 自定义业务异常
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 8566069710792258313L;
    private Integer code;
    private String message;

    public BusinessException(RestHttpStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
    }
}
