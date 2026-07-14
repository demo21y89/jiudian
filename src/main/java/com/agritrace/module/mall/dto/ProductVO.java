package com.agritrace.module.mall.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductVO {
    private Long id;
    private String name;
    private String category;
    private String origin;
    private String specifications;
    private String unit;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private String imageUrl;
    private String batchNo;
    private String farmingRecord;
    private String testReport;
    private String certNo;
    private String certStatus;
    private String certificationLabel;
    private Long farmerId;
    private Boolean published;
    private String createTime;
}