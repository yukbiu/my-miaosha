package com.zhbit.miaosha.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 会员模块Service
 */
public interface UmsMemberService {
    /**
     * 会员登录功能
     * @param username
     * @param password
     * @return 生成的用户令牌--Token
     */
    String login(String username, String password);

    /**
     * 获取用户信息
     * @param username
     * @return
     */
    UserDetails loadUserByUsername(String username);
}
