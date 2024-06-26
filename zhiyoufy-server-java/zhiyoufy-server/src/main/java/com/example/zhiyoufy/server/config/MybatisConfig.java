package com.example.zhiyoufy.server.config;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.example.zhiyoufy.server.dao", "com.example.zhiyoufy.mbg.mapper"})
public class MybatisConfig {
}
