package com.agritrace.mcp.skill;

import com.agritrace.mcp.spi.MCPPlugin;
import com.agritrace.module.order.entity.Order;
import com.agritrace.module.order.repository.OrderRepository;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class LogisticsQuerySkill implements MCPPlugin {

    private final OrderRepository orderRepository;

    public LogisticsQuerySkill(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public String getSkillName() {
        return "logistics_query";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String orderNo = (String) params.getOrDefault("order_no", "");
        Long orderId = params.get("order_id") instanceof Number ?
                ((Number) params.get("order_id")).longValue() : null;

        Optional<Order> orderOpt;
        if (orderNo != null && !orderNo.isEmpty()) {
            orderOpt = orderRepository.findById(Long.parseLong(orderNo.replace("ORD", "")));
        } else if (orderId != null) {
            orderOpt = orderRepository.findById(orderId);
        } else {
            return Map.of("error", "请提供订单号或订单ID");
        }

        if (orderOpt.isEmpty()) {
            return Map.of("error", "订单不存在");
        }

        Order order = orderOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("order_no", order.getOrderNo());
        response.put("status", order.getStatus());
        response.put("tracking_no", order.getTrackingNo());
        response.put("logistics_company", order.getLogisticsCompany());
        response.put("shipping_address", order.getShippingAddress());
        return response;
    }

    @Override
    public String getDescription() {
        return "物流查询：根据订单号查询物流轨迹与运输状态";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
