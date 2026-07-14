package com.agritrace.mcp.spi;

import java.util.*;

/**
 * MCP Plugin 加载器：基于 Java SPI 机制动态发现和加载所有 Skill
 */
public class MCPPluginLoader {

    private static final Map<String, MCPPlugin> pluginCache = new HashMap<>();

    public static Map<String, MCPPlugin> loadAllPlugins() {
        if (!pluginCache.isEmpty()) {
            return pluginCache;
        }

        ServiceLoader<MCPPlugin> loader = ServiceLoader.load(MCPPlugin.class);
        for (MCPPlugin plugin : loader) {
            pluginCache.put(plugin.getSkillName(), plugin);
        }
        return Collections.unmodifiableMap(pluginCache);
    }

    public static MCPPlugin getPlugin(String skillName) {
        if (pluginCache.isEmpty()) {
            loadAllPlugins();
        }
        return pluginCache.get(skillName);
    }

    public static List<String> getAllSkillNames() {
        if (pluginCache.isEmpty()) {
            loadAllPlugins();
        }
        return new ArrayList<>(pluginCache.keySet());
    }
}
