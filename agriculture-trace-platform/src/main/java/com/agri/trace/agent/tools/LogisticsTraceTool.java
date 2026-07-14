package com.agri.trace.agent.tools;

import com.agri.trace.entity.Order;
import com.agri.trace.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class LogisticsTraceTool implements McpTool {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public String getName() {
        return "logistics_trace";
    }

    @Override
    public String getDescription() {
        return "查询农产品物流运输路径，输入订单号或批次号，返回运输节点、时间等信息";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String orderNo = (String) params.get("orderNo");
        String batchNo = (String) params.get("batchNo");
        Map<String, Object> result = new LinkedHashMap<>();

        if (orderNo != null) {
            Order order = orderMapper.findByOrderNo(orderNo);
            if (order == null) {
                result.put("error", "未找到订单: " + orderNo);
                return result;
            }
            result.put("orderNo", order.getOrderNo());
            result.put("status", order.getStatus());
            result.put("logisticsNo", order.getLogisticsNo());
            result.put("logisticsCompany", order.getLogisticsCompany());
            result.put("receiverAddress", order.getReceiverAddress());
            result.put("message", "物流信息查询成功");
        } else if (batchNo != null) {
            // 通过批次号查找关联订单
            result.put("batchNo", batchNo);
            result.put("message", "请提供订单号以获取详细物流信息，批次号：" + batchNo);
        } else {
            result.put("error", "请输入订单号或批次号");
        }

        return result;
    }
}
