package com.agri.trace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private String status;          // PENDING/PAID/SHIPPED/COMPLETED/CANCELLED
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String logisticsNo;     // 物流单号
    private String logisticsCompany; // 物流公司
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
