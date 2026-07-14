package com.agritrace.module.trace.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trace_stages")
public class TraceStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long traceId;

    @Column(length = 50, nullable = false)
    private String stageName;

    private LocalDateTime stageTime;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 200)
    private String location;

    @Column(length = 200)
    private String operator;

    @Column(columnDefinition = "JSONB")
    private String detailData;

    private Integer sortOrder;

    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
