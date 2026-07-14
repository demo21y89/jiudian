package com.agri.trace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("trace_record")
public class TraceRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long batchId;
    private String recordType;
    private LocalDate recordTime;
    private String operator;
    private String content;
    private String detail;
    private String attachment;
    private LocalDateTime createTime;
}