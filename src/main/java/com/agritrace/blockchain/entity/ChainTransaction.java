package com.agritrace.blockchain.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChainTransaction {
    private String txHash;
    private long blockNumber;
    private String traceCode;
    private String dataHash;
    private LocalDateTime timestamp;
    private String status;
}
