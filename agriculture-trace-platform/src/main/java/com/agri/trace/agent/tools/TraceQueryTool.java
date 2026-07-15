package com.agri.trace.agent.tools;

import com.agri.trace.service.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class TraceQueryTool implements McpTool {

    @Autowired
    private TraceService traceService;

    @Override
    public String getName() {
        return "trace_query";
    }

    @Override
    public String getDescription() {
        return "查询农产品全链路溯源信息，输入批次号或商品名称，返回种植记录、加工记录、检测报告等";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String batchNo = (String) params.get("batchNo");
        if (batchNo == null || batchNo.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "批次号不能为空");
            return error;
        }
        return traceService.getFullTraceInfo(batchNo.trim());
    }
}
