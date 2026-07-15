package com.agri.trace.service.impl;

import cn.hutool.core.util.IdUtil;
import com.agri.trace.entity.Order;
import com.agri.trace.entity.OrderDetail;
import com.agri.trace.entity.Product;
import com.agri.trace.mapper.OrderDetailMapper;
import com.agri.trace.mapper.OrderMapper;
import com.agri.trace.mapper.ProductMapper;
import com.agri.trace.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Order createOrder(Long userId, List<Long> productIds, List<Integer> quantities,
                             String receiverName, String receiverPhone, String receiverAddress) {
        // 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNo(IdUtil.fastSimpleUUID().substring(0, 20).toUpperCase());
        order.setStatus("PENDING");
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 计算总价并创建明细
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productMapper.selectById(productIds.get(i));
            if (product == null || product.getStock() < quantities.get(i)) {
                throw new RuntimeException("商品库存不足: " + (product != null ? product.getName() : "未知"));
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setProductId(product.getId());
            detail.setProductName(product.getName());
            detail.setQuantity(quantities.get(i));
            detail.setPrice(product.getPrice());
            orderDetailMapper.insert(detail);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(quantities.get(i))));

            // 扣减库存
            product.setStock(product.getStock() - quantities.get(i));
            productMapper.updateById(product);
        }

        order.setTotalAmount(total);
        orderMapper.updateById(order);
        return order;
    }

    @Override
    public IPage<Order> page(int page, int size, String status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(status != null && !status.isEmpty(), Order::getStatus, status)
                .orderByDesc(Order::getCreateTime);
        return orderMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderMapper.findByUserId(userId);
    }

    @Override
    public Order getById(Long id) {
        return orderMapper.selectById(id);
    }

    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        Order order = orderMapper.selectById(id);
        if (order == null) return false;
        order.setStatus(status);
        return orderMapper.updateById(order) > 0;
    }

    @Override
    @Transactional
    public boolean ship(Long id, String logisticsNo, String logisticsCompany) {
        Order order = orderMapper.selectById(id);
        if (order == null) return false;
        order.setStatus("SHIPPED");
        order.setLogisticsNo(logisticsNo);
        order.setLogisticsCompany(logisticsCompany);
        return orderMapper.updateById(order) > 0;
    }
}
