package com.zhbit.miaosha.config;


import com.zhbit.miaosha.security.config.SecurityConfig;
import com.zhbit.miaosha.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

@EnableWebSecurity
@Configuration
public class GlobalSecurityConfig extends SecurityConfig {
    @Autowired
    private UmsMemberService umsMemberService;

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        //获取登录用户信息——SpringSecurity上下文的用户详情
        return username -> umsMemberService.loadUserByUsername(username);
    }
}
