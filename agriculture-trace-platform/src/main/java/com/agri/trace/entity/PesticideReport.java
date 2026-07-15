package com.agri.trace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("pesticide_report")
public class PesticideReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long batchId;
    private String reportNo;
    private LocalDate testDate;
    private String testOrganization;    // 检测机构
    private String itemName;            // 检测项目名称
    private String result;              // 检测结果
    private String standardLimit;       // 标准限值 (GB 2763)
    private String unit;                // 单位
    private Boolean isCompliant;        // 是否达标
    private LocalDateTime createTime;
}
