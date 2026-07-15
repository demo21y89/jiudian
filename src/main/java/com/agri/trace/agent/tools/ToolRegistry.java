package com.agri.trace.agent.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class ToolRegistry {

    private final Map<String, McpTool> tools = new LinkedHashMap<>();

    @Autowired
    private List<McpTool> toolList;

    @PostConstruct
    public void init() {
        for (McpTool tool : toolList) {
            tools.put(tool.getName(), tool);
        }
    }

    public McpTool getTool(String name) {
        return tools.get(name);
    }

    public Map<String, McpTool> getAllTools() {
        return Collections.unmodifiableMap(tools);
    }

    public List<Map<String, String>> getToolDescriptions() {
        List<Map<String, String>> descs = new ArrayList<>();
        for (McpTool tool : tools.values()) {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("name", tool.getName());
            item.put("description", tool.getDescription());
            descs.add(item);
        }
        return descs;
    }
}
