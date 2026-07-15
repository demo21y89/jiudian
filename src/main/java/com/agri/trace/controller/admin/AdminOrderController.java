package com.agri.trace.controller.admin;

import com.agri.trace.dto.R;
import com.agri.trace.entity.Order;
import com.agri.trace.service.OrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/page")
    public R<IPage<Order>> page(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) String status) {
        return R.ok(orderService.page(page, size, status));
    }

    @GetMapping("/{id}")
    public R<Order> detail(@PathVariable Long id) {
        return R.ok(orderService.getById(id));
    }

    @PostMapping("/{id}/ship")
    public R<?> ship(@PathVariable Long id, @RequestBody ShipRequest req) {
        orderService.ship(id, req.logisticsNo, req.logisticsCompany);
        return R.ok();
    }

    @PostMapping("/{id}/complete")
    public R<?> complete(@PathVariable Long id) {
        orderService.updateStatus(id, "COMPLETED");
        return R.ok();
    }

    static class ShipRequest {
        public String logisticsNo;
        public String logisticsCompany;
    }
}
