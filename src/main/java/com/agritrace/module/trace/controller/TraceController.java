package com.agritrace.module.trace.controller;

import com.agritrace.common.response.ApiResult;
import com.agritrace.module.trace.dto.TraceQueryRequest;
import com.agritrace.module.trace.dto.TraceVO;
import com.agritrace.module.trace.service.TraceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trace")
public class TraceController {

    private final TraceService traceService;

    public TraceController(TraceService traceService) {
        this.traceService = traceService;
    }

    @PostMapping("/query")
    public ApiResult<TraceVO> query(@RequestBody Map<String, Object> body) {
        TraceQueryRequest request = new TraceQueryRequest();
        request.setTraceCode((String) body.getOrDefault("trace_code", body.getOrDefault("traceCode", "")));
        request.setBatchNo((String) body.getOrDefault("batch_no", body.getOrDefault("batchNo", "")));
        return ApiResult.success(traceService.queryTrace(request));
    }
}