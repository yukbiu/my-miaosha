package com.zhbit.miaosha.model.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录返回结果
 */
@Data
public class LoginResult implements Serializable {
    private static final long serialVersionUID = 3250673120861595892L;

    private String token;

    public LoginResult(String token) {
        this.token = token;
    }
}
