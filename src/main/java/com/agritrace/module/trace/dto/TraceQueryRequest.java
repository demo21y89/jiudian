package com.agritrace.module.trace.dto;

import lombok.Data;

@Data
public class TraceQueryRequest {
    private String traceCode;
    private String batchNo;
}