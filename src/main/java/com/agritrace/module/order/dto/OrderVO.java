package com.agritrace.module.order.dto;

import lombok.Data;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private java.math.BigDecimal totalPrice;
    private String status;
    private String shippingAddress;
    private String trackingNo;
    private String logisticsCompany;
    private String remark;
    private java.time.LocalDateTime createTime;
}
