package com.agritrace.mcp.skill;

import com.agritrace.knowledge.dto.KnowledgeRetrieveRequest;
import com.agritrace.knowledge.dto.KnowledgeVO;
import com.agritrace.knowledge.service.KnowledgeService;
import com.agritrace.mcp.spi.MCPPlugin;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class RegulationQuerySkill implements MCPPlugin {

    private final KnowledgeService knowledgeService;

    public RegulationQuerySkill(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @Override
    public String getSkillName() {
        return "regulation_query";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String query = (String) params.getOrDefault("query", "食品安全法规");
        String category = (String) params.getOrDefault("category", "法规");

        KnowledgeRetrieveRequest request = new KnowledgeRetrieveRequest();
        request.setQuery(query);
        request.setCategory(category);
        request.setTopK(5);

        List<KnowledgeVO> results = knowledgeService.retrieve(request);

        Map<String, Object> response = new HashMap<>();
        response.put("query", query);
        response.put("results", results);
        response.put("total", results.size());
        return response;
    }

    @Override
    public String getDescription() {
        return "法规检索：查询食品安全法规、种植标准和质检标准";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
