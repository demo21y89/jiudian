package com.agritrace.mcp.spi;

import java.util.Map;

/**
 * MCP Skill SPI 接口
 * 所有业务技能基于 Java SPI 机制可插拔加载
 */
public interface MCPPlugin {

    String getSkillName();

    Map<String, Object> execute(Map<String, Object> params);

    String getDescription();

    String getVersion();
}
