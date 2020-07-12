package com.zhbit.miaosha.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 登录请求传入参数
 */
@Data
public class LoginParams {
    /**
     * 登录邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}
