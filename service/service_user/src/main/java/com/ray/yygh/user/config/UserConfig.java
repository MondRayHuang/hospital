package com.ray.yygh.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ray.yygh.user.mapper")
public class UserConfig {
}
