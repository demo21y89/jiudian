package com.agri.trace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("batch")
public class Batch {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private Long productId;
    private LocalDate produceDate;
    private LocalDate harvestDate;
    private Integer quantity;
    private String farmAddress;     // 生产基地地址
    private String farmArea;        // 种植面积
    private String soilType;        // 土壤类型
    private LocalDateTime createTime;
}
