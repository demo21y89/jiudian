package com.agritrace.mcp.skill;

import com.agritrace.mcp.spi.MCPPlugin;
import com.agritrace.module.mall.entity.Product;
import com.agritrace.module.mall.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RecommendSkill implements MCPPlugin {

    private final ProductRepository productRepository;

    public RecommendSkill(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public String getSkillName() {
        return "recommend";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String category = (String) params.getOrDefault("category", "");
        String keyword = (String) params.getOrDefault("keyword", "");
        int limit = params.get("limit") instanceof Number ? ((Number) params.get("limit")).intValue() : 5;

        Page<Product> products;
        if (!category.isEmpty()) {
            products = productRepository.findByFilters(category, null, null, PageRequest.of(0, limit));
        } else if (!keyword.isEmpty()) {
            products = productRepository.findByPublishedTrueAndNameContaining(keyword, PageRequest.of(0, limit));
        } else {
            products = productRepository.findByPublishedTrue(PageRequest.of(0, limit));
        }

        List<Map<String, Object>> items = products.stream().map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
            item.put("price", p.getPrice());
            item.put("origin", p.getOrigin());
            item.put("category", p.getCategory());
            item.put("certification", p.getCertificationLabel());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("recommendations", items);
        response.put("total", items.size());
        return response;
    }

    @Override
    public String getDescription() {
        return "智能推荐：根据用户偏好和历史查询推荐高透明度优质农产品";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
