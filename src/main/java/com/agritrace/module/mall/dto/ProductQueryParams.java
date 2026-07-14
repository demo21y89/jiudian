package com.agritrace.module.mall.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductQueryParams {
    private String keyword;
    private String category;
    private String origin;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String certificationLabel;
    private int page = 1;
    private int size = 20;
}
