package com.agri.trace.service;

import com.agri.trace.entity.Order;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities,
                      String receiverName, String receiverPhone, String receiverAddress);
    IPage<Order> page(int page, int size, String status);
    List<Order> findByUserId(Long userId);
    Order getById(Long id);
    boolean updateStatus(Long id, String status);
    boolean ship(Long id, String logisticsNo, String logisticsCompany);
}
