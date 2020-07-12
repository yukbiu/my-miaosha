package com.zhbit.miaosha.controller;

import com.zhbit.miaosha.common.RestResponse;
import com.zhbit.miaosha.model.dto.LoginParams;
import com.zhbit.miaosha.model.result.LoginResult;
import com.zhbit.miaosha.service.UmsMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证 Controller，包括用户注册，用户登录请求
 */
@Slf4j
@RestController
public class UmsMemberController {

    @Autowired
    private UmsMemberService umsMemberService;

    @PostMapping("/api/login")
    public RestResponse login(@Valid @RequestBody LoginParams loginParams) {

        String token = umsMemberService.login(loginParams.getEmail(), loginParams.getPassword());

        return RestResponse.success("登录成功", new LoginResult(token));
    }
}
