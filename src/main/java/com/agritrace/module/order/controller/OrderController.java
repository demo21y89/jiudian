package com.agritrace.module.order.controller;

import com.agritrace.common.response.ApiResult;
import com.agritrace.module.order.dto.OrderCreateRequest;
import com.agritrace.module.order.dto.OrderVO;
import com.agritrace.module.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResult<OrderVO> create(@Valid @RequestBody OrderCreateRequest request) {
        return ApiResult.success(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ApiResult<OrderVO> getById(@PathVariable Long id) {
        return ApiResult.success(orderService.getOrderById(id));
    }
}
