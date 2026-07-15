package com.agri.trace.controller.api;

import com.agri.trace.dto.R;
import com.agri.trace.entity.Order;
import com.agri.trace.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public R<Order> create(@RequestBody CreateOrderRequest req, Authentication auth) {
        try {
            Long userId = (Long) auth.getPrincipal();
            Order order = orderService.createOrder(userId, req.productIds, req.quantities,
                    req.receiverName, req.receiverPhone, req.receiverAddress);
            return R.ok(order);
        } catch (RuntimeException e) {
            return R.error(400, e.getMessage());
        }
    }

    @GetMapping("/list")
    public R<List<Order>> list(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return R.ok(orderService.findByUserId(userId));
    }

    @GetMapping("/{id}")
    public R<Order> detail(@PathVariable Long id) {
        return R.ok(orderService.getById(id));
    }

    @PostMapping("/{id}/pay")
    public R<?> pay(@PathVariable Long id) {
        orderService.updateStatus(id, "PAID");
        return R.ok();
    }

    @PostMapping("/{id}/cancel")
    public R<?> cancel(@PathVariable Long id) {
        orderService.updateStatus(id, "CANCELLED");
        return R.ok();
    }

    static class CreateOrderRequest {
        public List<Long> productIds;
        public List<Integer> quantities;
        public String receiverName;
        public String receiverPhone;
        public String receiverAddress;
    }
}
