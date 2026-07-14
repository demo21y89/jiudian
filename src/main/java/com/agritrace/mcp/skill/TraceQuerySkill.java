package com.agritrace.mcp.skill;

import com.agritrace.mcp.spi.MCPPlugin;
import com.agritrace.module.trace.dto.TraceQueryRequest;
import com.agritrace.module.trace.dto.TraceVO;
import com.agritrace.module.trace.service.TraceService;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class TraceQuerySkill implements MCPPlugin {

    private final TraceService traceService;

    public TraceQuerySkill(TraceService traceService) {
        this.traceService = traceService;
    }

    @Override
    public String getSkillName() {
        return "trace_query";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String traceCode = (String) params.getOrDefault("trace_code", "");
        String batchNo = (String) params.getOrDefault("batch_no", "");

        TraceQueryRequest request = new TraceQueryRequest();
        request.setTraceCode(traceCode);
        request.setBatchNo(batchNo);

        TraceVO result = traceService.queryTrace(request);

        Map<String, Object> response = new HashMap<>();
        response.put("product_name", result.getProductName());
        response.put("origin", result.getOrigin());
        response.put("cert_valid", result.getCertValid());
        response.put("stages", result.getStages());
        response.put("scan_count", result.getScanCount());
        response.put("tx_hash", result.getTxHash());
        return response;
    }

    @Override
    public String getDescription() {
        return "农产品溯源查询：根据溯源码或批次号查询完整溯源链路信息";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
