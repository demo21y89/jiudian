package com.agritrace.blockchain.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChainRecord {
    private String traceCode;
    private String txHash;
    private long blockNumber;
    private LocalDateTime timestamp;
    private boolean valid;
}
