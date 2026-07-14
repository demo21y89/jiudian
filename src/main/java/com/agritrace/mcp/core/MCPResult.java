package com.agritrace.mcp.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class MCPResult {
    private String status;
    private Object data;
    private long executionTime;

    public static MCPResult success(Object data) {
        return new MCPResult("success", data, 0);
    }

    public static MCPResult error(String message) {
        return new MCPResult("error", Map.of("error", message), 0);
    }
}
