package com.zhbit.miaosha.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@MapperScan(value = "com.zhbit.miaosha.dao")
@Configuration
public class MybatisConfig {
}
