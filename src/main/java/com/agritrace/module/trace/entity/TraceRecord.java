package com.agritrace.module.trace.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trace_records")
public class TraceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String traceCode;

    @Column(nullable = false, length = 50)
    private String batchNo;

    @Column(nullable = false, length = 200)
    private String productName;

    @Column(length = 200)
    private String origin;

    @Column(length = 50)
    private String certNo;

    @Column(length = 20)
    private String certStatus;

    @Column(columnDefinition = "JSONB")
    private String stages;

    @Column(columnDefinition = "JSONB")
    private String pesticideReport;

    @Column(length = 100)
    private String txHash;

    @Column(nullable = false)
    private Boolean valid = true;

    @Column(nullable = false)
    private Integer scanCount = 0;

    @Column(updatable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
