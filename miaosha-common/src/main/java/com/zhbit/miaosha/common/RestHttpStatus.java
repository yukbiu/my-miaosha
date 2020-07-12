package com.zhbit.miaosha.common;

/**
 * 全局状态码
 */
public enum RestHttpStatus {

    SUCCESS(200, "OK"),
    FAILED(-1,"操作失败"),
    BAD_REQUEST(400,"非法参数"),
    UNAUTHORIZED(401,"未登录或Token已过期"),
    FORBIDDEN(403, "没有相关权限"),
    SYSTEM_ERROR(500,"系统错误")
    ;

    private Integer code;
    private String message;

    RestHttpStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
