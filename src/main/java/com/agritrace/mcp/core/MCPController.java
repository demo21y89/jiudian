package com.agritrace.mcp.core;

import com.agritrace.common.response.ApiResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mcp/skill")
public class MCPController {

    private final MCPDispatcher dispatcher;

    public MCPController(MCPDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping("/invoke")
    public ApiResult<MCPResult> invoke(@RequestBody Map<String, Object> request) {
        String skill = (String) request.get("skill");
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) request.getOrDefault("params", Map.of());
        MCPResult result = dispatcher.invoke(skill, params);
        return ApiResult.success(result);
    }

    @GetMapping("/list")
    public ApiResult<List<Map<String, Object>>> listSkills() {
        return ApiResult.success(dispatcher.listAvailableSkills());
    }
}
