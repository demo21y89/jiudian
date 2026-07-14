package com.agritrace.mcp.core;

import com.agritrace.mcp.spi.MCPPlugin;
import com.agritrace.mcp.spi.MCPPluginLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MCP 调度器：接收 Agent 请求，调度对应的 Skill 执行
 */
@Service
public class MCPDispatcher {

    private static final Logger log = LoggerFactory.getLogger(MCPDispatcher.class);

    private final Map<String, MCPPlugin> plugins = new ConcurrentHashMap<>();

    public MCPDispatcher() {
        // 尝试通过 SPI 加载插件，注册内置 Skill 作为 fallback
        try {
            Map<String, MCPPlugin> loaded = MCPPluginLoader.loadAllPlugins();
            plugins.putAll(loaded);
            log.info("通过 SPI 加载了 {} 个 MCP Skill", loaded.size());
        } catch (Exception e) {
            log.warn("SPI 加载失败，将使用内置注册方式: {}", e.getMessage());
        }
    }

    public void registerPlugin(MCPPlugin plugin) {
        plugins.put(plugin.getSkillName(), plugin);
    }

    public MCPResult invoke(String skill, Map<String, Object> params) {
        long start = System.currentTimeMillis();
        MCPPlugin plugin = plugins.get(skill);
        if (plugin == null) {
            log.warn("未找到 Skill: {}", skill);
            return MCPResult.error("未找到 Skill: " + skill);
        }

        try {
            Map<String, Object> result = plugin.execute(params);
            long duration = System.currentTimeMillis() - start;
            log.info("MCP Skill [{}] 执行完成，耗时 {}ms", skill, duration);
            MCPResult mcpResult = MCPResult.success(result);
            mcpResult.setExecutionTime(duration);
            return mcpResult;
        } catch (Exception e) {
            log.error("MCP Skill [{}] 执行异常: {}", skill, e.getMessage());
            return MCPResult.error(e.getMessage());
        }
    }

    public List<Map<String, Object>> listAvailableSkills() {
        List<Map<String, Object>> skills = new ArrayList<>();
        for (Map.Entry<String, MCPPlugin> entry : plugins.entrySet()) {
            Map<String, Object> info = new HashMap<>();
            info.put("name", entry.getKey());
            info.put("description", entry.getValue().getDescription());
            info.put("version", entry.getValue().getVersion());
            skills.add(info);
        }
        return skills;
    }

    public List<String> getSkillNames() {
        return new ArrayList<>(plugins.keySet());
    }
}
