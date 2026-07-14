package com.agritrace.mcp.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MCPTool {
    private String name;
    private String description;
    private Map<String, Object> inputSchema;
}
