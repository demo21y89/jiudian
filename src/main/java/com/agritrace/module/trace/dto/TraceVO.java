package com.agritrace.module.trace.dto;

import lombok.Data;

@Data
public class TraceVO {
    private String traceCode;
    private String batchNo;
    private String productName;
    private String origin;
    private String certNo;
    private Boolean certValid;
    private Object stages;
    private Object pesticideReport;
    private String txHash;
    private Integer scanCount;
}
