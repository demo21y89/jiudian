package com.agritrace.mcp.skill;

import com.agritrace.knowledge.dto.KnowledgeRetrieveRequest;
import com.agritrace.knowledge.dto.KnowledgeVO;
import com.agritrace.knowledge.service.KnowledgeService;
import com.agritrace.mcp.spi.MCPPlugin;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class PesticideAnalysisSkill implements MCPPlugin {

    private final KnowledgeService knowledgeService;

    public PesticideAnalysisSkill(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @Override
    public String getSkillName() {
        return "pesticide_analysis";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String product = (String) params.getOrDefault("product", "");
        String query = (product.isEmpty() ? "" : product + " ") + "农药残留标准 农残限量";

        KnowledgeRetrieveRequest request = new KnowledgeRetrieveRequest();
        request.setQuery(query);
        request.setTopK(3);

        List<KnowledgeVO> knowledgeResults = knowledgeService.retrieve(request);

        Map<String, Object> response = new HashMap<>();
        response.put("product", product);
        response.put("analysis", knowledgeResults.isEmpty() ?
                "未找到相关农残标准信息" : "已检索到相关标准");
        response.put("related_standards", knowledgeResults);
        return response;
    }

    @Override
    public String getDescription() {
        return "农药残留分析：查询农残标准并分析产品是否达标";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
