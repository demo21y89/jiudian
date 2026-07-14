package com.agritrace.module.order.dto;

import lombok.Data;

@Data
public class OrderCreateRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String shippingAddress;
    private String remark;
}
