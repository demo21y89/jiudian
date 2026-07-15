package com.agri.trace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;        // 分类：水果/蔬菜/粮食/茶叶/畜禽
    private String origin;          // 产地
    private String spec;            // 规格
    private BigDecimal price;
    private Integer stock;
    private String batchNo;         // 溯源批次号
    private String imageUrl;        // 商品图片
    private String description;     // 商品描述
    private String traceLevel;      // 溯源等级标识
    @TableLogic
    private Integer deleted;
    private Integer status;         // 0-下架 1-上架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
