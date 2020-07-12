package com.zhbit.miaosha.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 */
@Data
public class RestResponse<T> implements Serializable {
    private static final long serialVersionUID = 3464914775687134751L;
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回内容
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    private RestResponse() {
    }

    private RestResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private RestResponse(RestHttpStatus status, T data) {
        this.code = status.getCode();
        this.message = status.getMessage();
        this.data = data;
    }

//    /**
//     * 无数据默认成功返回结果
//     * @return
//     */
//    public static <T> RestResponse<T> success() {
//        return new RestResponse<>(RestHttpStatus.SUCCESS,null);
//    }

    /**
     * 成功返回结果
     * @param data 获取的数据
     */
    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(RestHttpStatus.SUCCESS, data);
    }

    /**
     * 成功返回结果
     * @param data 获取的数据
     * @param message  提示信息
     */
    public static <T> RestResponse<T> success(String message, T data) {
        return new RestResponse<>(RestHttpStatus.SUCCESS.getCode(), message, data);
    }

    /**
     * 操作失败返回结果
     * @param message 提示信息
     * @return
     */
    public static <T> RestResponse<T> failed(String message) {
        return new RestResponse<>(RestHttpStatus.FAILED.getCode(), message, null);
    }

    /**
     * 未登录或登录已过期
     * @return
     */
    public static <T> RestResponse<T> unauthorized() {
        return new RestResponse<>(RestHttpStatus.UNAUTHORIZED, null);
    }

    /**
     * 认证失败
     * @param message 失败信息
     * @return
     */
    public static <T> RestResponse<T> unauthorized(String message) {
        return new RestResponse<>(RestHttpStatus.UNAUTHORIZED.getCode(), message, null);
    }

    /**
     * 没有相关权限
     * @return
     */
    public static <T> RestResponse<T> forbidden() {
        return new RestResponse<>(RestHttpStatus.FORBIDDEN, null);
    }

    /**
     * 系统错误
     * @return
     */
    public static <T> RestResponse<T> error() {
        return new RestResponse<>(RestHttpStatus.SYSTEM_ERROR, null);
    }

}
