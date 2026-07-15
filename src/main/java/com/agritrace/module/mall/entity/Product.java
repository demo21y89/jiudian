package com.agritrace.module.mall.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 100)
    private String category;

    @Column(length = 200)
    private String origin;

    @Column(length = 50)
    private String specifications;

    @Column(length = 50)
    private String unit = "斤";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 2000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 50)
    private String batchNo;

    @Column(length = 2000)
    private String farmingRecord;

    @Column(length = 2000)
    private String testReport;

    @Column(length = 50)
    private String certNo;

    @Column(length = 20)
    private String certStatus;

    @Column(length = 100)
    private String certificationLabel;

    @Column(columnDefinition = "LONGTEXT")
    private String extraInfo;

    @Column(nullable = false)
    private Boolean published = false;

    @Column(name = "farmer_id")
    private Long farmerId;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(updatable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
