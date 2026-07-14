package com.agritrace.config;

import com.agritrace.mcp.skill.*;
import com.agritrace.mcp.spi.MCPPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MCP SPI 手动注册配置
 * 将内置 Skill 注册为 SPI 插件（同时支持自动 SPI 发现）
 */
@Configuration
public class MCPSPIConfig {

    @Bean
    public MCPPlugin traceQueryPlugin(TraceQuerySkill skill) {
        return skill;
    }

    @Bean
    public MCPPlugin pesticidePlugin(PesticideAnalysisSkill skill) {
        return skill;
    }

    @Bean
    public MCPPlugin logisticsPlugin(LogisticsQuerySkill skill) {
        return skill;
    }

    @Bean
    public MCPPlugin recommendPlugin(RecommendSkill skill) {
        return skill;
    }

    @Bean
    public MCPPlugin regulationPlugin(RegulationQuerySkill skill) {
        return skill;
    }

    @Bean
    public MCPPlugin certPlugin(CertVerificationSkill skill) {
        return skill;
    }
}
