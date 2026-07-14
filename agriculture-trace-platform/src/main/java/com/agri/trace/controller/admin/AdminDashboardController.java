package com.agri.trace.controller.admin;

import com.agri.trace.dto.R;
import com.agri.trace.entity.Order;
import com.agri.trace.entity.Product;
import com.agri.trace.mapper.AgentDialogMapper;
import com.agri.trace.mapper.OrderMapper;
import com.agri.trace.mapper.ProductMapper;
import com.agri.trace.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AgentDialogMapper agentDialogMapper;

    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 商品总数
        stats.put("productCount", productMapper.selectCount(null));

        // 订单总数
        stats.put("orderCount", orderMapper.selectCount(null));

        // 用户总数
        stats.put("userCount", userMapper.selectCount(null));

        // 对话总数
        stats.put("dialogCount", agentDialogMapper.selectCount(null));

        return R.ok(stats);
    }

    @GetMapping("/sales-report")
    public R<Map<String, Object>> salesReport(@RequestParam(required = false) String period) {
        Map<String, Object> report = new LinkedHashMap<>();
        List<Order> orders = orderMapper.selectList(null);
        double totalSales = orders.stream()
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount().doubleValue() : 0)
                .sum();
        report.put("totalSales", totalSales);
        report.put("orderCount", orders.size());
        return R.ok(report);
    }
}
