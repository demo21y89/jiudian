package com.agritrace.config;

import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置 - 使用 Spring Boot 自动配置
 * H2 开发模式：application-h2.yml 自动生效
 * PostgreSQL 生产模式：配置 spring.datasource 即可
 */
@Configuration
public class DatabaseConfig {
    // Spring Boot 自动配置 datasource，无需手动创建 Bean
}
