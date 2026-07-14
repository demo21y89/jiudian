package com.agri.trace.agent.tools;

import com.agri.trace.entity.Product;
import com.agri.trace.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ProductRecommendTool implements McpTool {

    @Autowired
    private ProductService productService;

    @Override
    public String getName() {
        return "product_recommend";
    }

    @Override
    public String getDescription() {
        return "根据用户偏好推荐农产品，输入关键词（如有机、水果、山东等），返回推荐商品列表";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        if (keyword == null) keyword = "";
        Map<String, Object> result = new LinkedHashMap<>();

        List<Product> products;
        if (keyword.trim().isEmpty()) {
            products = productService.list();
        } else {
            products = productService.search(keyword);
        }

        List<Map<String, Object>> productList = new ArrayList<>();
        for (Product p : products) {
            if (p.getStatus() == null || p.getStatus() != 1) continue;
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
            item.put("origin", p.getOrigin());
            item.put("price", p.getPrice());
            item.put("category", p.getCategory());
            item.put("traceLevel", p.getTraceLevel());
            item.put("stock", p.getStock());
            productList.add(item);
        }

        result.put("keyword", keyword);
        result.put("count", productList.size());
        result.put("products", productList);
        return result;
    }
}
