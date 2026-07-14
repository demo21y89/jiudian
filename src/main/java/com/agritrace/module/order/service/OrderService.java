package com.agritrace.module.order.service;

import com.agritrace.common.exception.BusinessException;
import com.agritrace.common.util.CodeGenerator;
import com.agritrace.module.mall.entity.Product;
import com.agritrace.module.mall.repository.ProductRepository;
import com.agritrace.module.order.dto.OrderCreateRequest;
import com.agritrace.module.order.dto.OrderVO;
import com.agritrace.module.order.entity.Order;
import com.agritrace.module.order.repository.OrderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderVO createOrder(OrderCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("商品不存在"));

        if (product.getStock() < request.getQuantity()) {
            throw new BusinessException("库存不足");
        }

        Order order = new Order();
        order.setOrderNo(CodeGenerator.generateOrderNo());
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        order.setStatus("待付款");
        order.setShippingAddress(request.getShippingAddress());
        order.setRemark(request.getRemark());

        // 扣减库存
        product.setStock(product.getStock() - request.getQuantity());
        productRepository.save(product);

        order = orderRepository.save(order);
        OrderVO vo = toOrderVO(order);
        vo.setProductName(product.getName());
        return vo;
    }

    public OrderVO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("订单不存在"));
        return toOrderVO(order);
    }

    private OrderVO toOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        return vo;
    }
}
