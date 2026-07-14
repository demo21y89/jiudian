package com.agritrace.agent;

import com.agritrace.agent.memory.ConversationMemory;
import com.agritrace.knowledge.dto.KnowledgeRetrieveRequest;
import com.agritrace.knowledge.dto.KnowledgeVO;
import com.agritrace.knowledge.service.KnowledgeService;
import com.agritrace.mcp.core.MCPDispatcher;
import com.agritrace.mcp.core.MCPResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Agent 编排器：意图识别、任务拆解、工具调度
 */
@Service
public class AgentOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AgentOrchestrator.class);

    private final MCPDispatcher mcpDispatcher;
    private final KnowledgeService knowledgeService;
    private final Map<String, ConversationMemory> sessions = new ConcurrentHashMap<>();

    public AgentOrchestrator(MCPDispatcher mcpDispatcher, KnowledgeService knowledgeService) {
        this.mcpDispatcher = mcpDispatcher;
        this.knowledgeService = knowledgeService;
    }

    public ConversationMemory getOrCreateSession(String sessionId) {
        return sessions.computeIfAbsent(sessionId, ConversationMemory::new);
    }

    public AgentResponse processQuery(String sessionId, String query, Long userId) {
        ConversationMemory memory = getOrCreateSession(sessionId);
        memory.addMessage("user", query);

        long startTime = System.currentTimeMillis();

        // 1. 意图识别（规则引擎，生产环境使用大模型）
        String intent = classifyIntent(query);
        log.info("Session [{}] 意图识别: {}", sessionId, intent);

        // 2. 工具调度与执行
        List<String> toolsUsed = new ArrayList<>();
        List<Map<String, Object>> sources = new ArrayList<>();
        StringBuilder answer = new StringBuilder();

        switch (intent) {
            case "trace_query" -> handleTraceQuery(query, answer, toolsUsed, sources);
            case "pesticide" -> handlePesticideQuery(query, answer, toolsUsed, sources);
            case "logistics" -> handleLogisticsQuery(query, answer, toolsUsed, sources);
            case "regulation" -> handleRegulationQuery(query, answer, toolsUsed, sources);
            case "cert_verify" -> handleCertVerify(query, answer, toolsUsed, sources);
            case "recommend" -> handleRecommend(query, answer, toolsUsed, sources);
            default -> handleGeneralQuery(query, answer, toolsUsed, sources);
        }

        // 3. 更新对话记忆
        memory.addMessage("assistant", answer.toString());

        // 4. 更新用户偏好（用于推荐）
        updatePreferences(memory, intent, query);

        AgentResponse response = new AgentResponse();
        response.setSessionId(sessionId);
        response.setContent(answer.toString());
        response.setToolsUsed(toolsUsed);
        response.setSources(sources);
        response.setTokenUsed(estimateTokens(answer.toString()));
        response.setExecutionTime(System.currentTimeMillis() - startTime);
        return response;
    }

    private String classifyIntent(String query) {
        String lower = query.toLowerCase();
        if (lower.contains("溯源") || lower.contains("追溯") || lower.contains("trace") || lower.contains("批次")) {
            return "trace_query";
        }
        if (lower.contains("农残") || lower.contains("农药") || lower.contains("pesticide") || lower.contains("达标")) {
            return "pesticide";
        }
        if (lower.contains("物流") || lower.contains("运输") || lower.contains("配送") || lower.contains("logistics")) {
            return "logistics";
        }
        if (lower.contains("法规") || lower.contains("标准") || lower.contains("regulation") || lower.contains("合格")) {
            return "cert_verify";
        }
        if (lower.contains("推荐") || lower.contains("推荐") || lower.contains("推荐") || lower.contains("recommend")) {
            return "recommend";
        }
        if (lower.contains("法规") || lower.contains("法律") || lower.contains("条例") || lower.contains("standard")) {
            return "regulation";
        }
        return "general";
    }

    private void handleTraceQuery(String query, StringBuilder answer, List<String> tools, List<Map<String, Object>> sources) {
        tools.add("trace_query");
        Map<String, Object> params = new HashMap<>();
        // 简单提取批次号或溯源码
        if (query.contains("B2026") || query.contains("批次")) {
            params.put("batch_no", extractCode(query, "B\\d{8}\\d{2}"));
        } else {
            params.put("trace_code", extractCode(query, "TRC\\d{8}\\d{4}"));
        }

        MCPResult result = mcpDispatcher.invoke("trace_query", params);
        if ("success".equals(result.getStatus()) && result.getData() instanceof Map data) {
            answer.append("🔍 溯源查询结果：\n");
            answer.append("  - 产品名称：").append(data.get("product_name")).append("\n");
            answer.append("  - 产地：").append(data.get("origin")).append("\n");
            answer.append("  - 合格证状态：").append(data.get("cert_valid")).append("\n");
            answer.append("  - 扫码次数：").append(data.get("scan_count")).append("\n");
            answer.append("  - 区块链存证：").append(data.get("tx_hash")).append("\n");
            addSource(sources, "trace_data", query.contains("批次") ? "batch_no" : "trace_code");
        } else {
            answer.append("未找到对应的溯源信息，请检查溯源码或批次号是否正确。");
        }
    }

    private void handlePesticideQuery(String query, StringBuilder answer, List<String> tools, List<Map<String, Object>> sources) {
        tools.add("pesticide_analysis");
        MCPResult result = mcpDispatcher.invoke("pesticide_analysis", Map.of("product", query));
        if ("success".equals(result.getStatus()) && result.getData() instanceof Map data) {
            answer.append("🧪 农残分析结果：\n");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> standards = (List<Map<String, Object>>) data.get("related_standards");
            if (standards != null && !standards.isEmpty()) {
                answer.append("  相关标准：\n");
                standards.forEach(s -> answer.append("    - ").append(s.get("title")).append("\n"));
            } else {
                answer.append("  未检索到相关农残标准信息。\n");
            }
            addSource(sources, "knowledge", "GB 2763-2021");
        }
    }

    private void handleLogisticsQuery(String query, StringBuilder answer, List<String> tools, List<Map<String, Object>> sources) {
        tools.add("logistics_query");
        String orderNo = extractCode(query, "ORD\\d{14}");
        MCPResult result = mcpDispatcher.invoke("logistics_query", Map.of("order_no", orderNo));
        if ("success".equals(result.getStatus()) && result.getData() instanceof Map data) {
            answer.append("🚚 物流信息：\n");
            answer.append("  - 订单号：").append(data.get("order_no")).append("\n");
            answer.append("  - 状态：").append(data.get("status")).append("\n");
            answer.append("  - 物流公司：").append(data.get("logistics_company")).append("\n");
            answer.append("  - 运单号：").append(data.get("tracking_no")).append("\n");
            addSource(sources, "logistics", orderNo);
        } else {
            answer.append("未查询到物流信息。");
        }
    }

    private void handleRegulationQuery(String query, StringBuilder answer, List<String> tools, List<Map<String, Object>> sources) {
        tools.add("regulation_query");
        KnowledgeRetrieveRequest request = new KnowledgeRetrieveRequest();
        request.setQuery(query.replace("法规", "").replace("标准", "").trim());
        request.setCategory("法规");
        request.setTopK(3);

        List<KnowledgeVO> results = knowledgeService.retrieve(request);
        if (!results.isEmpty()) {
            answer.append("📚 相关法规/标准：\n");
            results.forEach(r -> {
                answer.append("  - ").append(r.getTitle()).append("\n");
                answer.append("    来源：").append(r.getSource()).append("\n");
                addSource(sources, "knowledge", r.getSource());
            });
        } else {
            answer.append("未找到相关法规信息。");
        }
    }

    private void handleCertVerify(String query, StringBuilder answer, List<String> tools, List<Map<String, Object>> sources) {
        tools.add("cert_verification");
        String certNo = extractCode(query, "CQ\\d{12}");
        MCPResult result = mcpDispatcher.invoke("cert_verification", Map.of("cert_no", certNo));
        if ("success".equals(result.getStatus()) && result.getData() instanceof Map data) {
            Boolean valid = (Boolean) data.get("valid");
            answer.append(valid ? "✅ " : "❌ ");
            answer.append("合格证验真结果：\n");
            answer.append("  - 证书编号：").append(data.get("cert_no")).append("\n");
            answer.append("  - 状态：").append(valid ? "有效" : "无效").append("\n");
            answer.append("  - 产品：").append(data.get("product_name")).append("\n");
            answer.append("  - 区块链存证：").append(data.get("tx_hash")).append("\n");
            addSource(sources, "cert", certNo);
        } else {
            answer.append("未找到合格证信息。");
        }
    }

    private void handleRecommend(String query, StringBuilder answer, List<String> tools, List<Map<String, Object>> sources) {
        tools.add("recommend");
        String category = query.contains("水果") || query.contains("苹果") ? "水果" :
                          query.contains("蔬菜") || query.contains("番茄") ? "蔬菜" : "";
        MCPResult result = mcpDispatcher.invoke("recommend", Map.of("category", category, "limit", 5));
        if ("success".equals(result.getStatus()) && result.getData() instanceof Map data) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("recommendations");
            if (items != null && !items.isEmpty()) {
                answer.append("🌟 为您推荐以下农产品：\n");
                items.forEach(item -> {
                    answer.append("  - ").append(item.get("name"))
                          .append(" | ¥").append(item.get("price"))
                          .append(" | ").append(item.get("origin")).append("\n");
                });
            }
        }
    }

    private void handleGeneralQuery(String query, StringBuilder answer, List<String> tools, List<Map<String, Object>> sources) {
        // 通用查询：尝试通过知识库检索回答
        KnowledgeRetrieveRequest request = new KnowledgeRetrieveRequest();
        request.setQuery(query);
        request.setTopK(3);

        List<KnowledgeVO> results = knowledgeService.retrieve(request);
        if (!results.isEmpty()) {
            answer.append("💡 根据知识库检索到以下信息：\n");
            results.forEach(r -> {
                answer.append("  - ").append(r.getTitle()).append("：")
                      .append(r.getContent()).append("\n");
                addSource(sources, "knowledge", r.getSource());
            });
        } else {
            answer.append("您好！我是农产品溯源智能助手。您可以向我询问以下问题：\n");
            answer.append("  - 溯源查询：\"山东苹果的溯源信息是什么？\"\n");
            answer.append("  - 农残分析：\"这个批次的农残是否达标？\"\n");
            answer.append("  - 物流查询：\"订单的物流状态如何？\"\n");
            answer.append("  - 法规检索：\"查询农产品质量安全法\"\n");
            answer.append("  - 合格证验真：\"验证合格证是否有效\"\n");
            answer.append("  - 产品推荐：\"推荐一些优质水果\"");
        }
    }

    private void updatePreferences(ConversationMemory memory, String intent, String query) {
        if ("recommend".equals(intent) || "trace_query".equals(intent)) {
            String category = query.contains("水果") || query.contains("苹果") ? "水果" :
                             query.contains("蔬菜") || query.contains("番茄") ? "蔬菜" :
                             query.contains("大米") || query.contains("谷物") ? "谷物" : "";
            if (!category.isEmpty()) {
                memory.setPreference("preferred_category", category);
            }
        }
    }

    private String extractCode(String query, String pattern) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(query);
        return m.find() ? m.group() : "";
    }

    private void addSource(List<Map<String, Object>> sources, String type, String ref) {
        Map<String, Object> source = new HashMap<>();
        source.put("type", type);
        source.put("ref", ref);
        sources.add(source);
    }

    private int estimateTokens(String text) {
        return text.length() / 2; // 粗略估计
    }

    public static class AgentResponse {
        private String sessionId;
        private String content;
        private List<String> toolsUsed;
        private List<Map<String, Object>> sources;
        private int tokenUsed;
        private long executionTime;

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public List<String> getToolsUsed() { return toolsUsed; }
        public void setToolsUsed(List<String> toolsUsed) { this.toolsUsed = toolsUsed; }
        public List<Map<String, Object>> getSources() { return sources; }
        public void setSources(List<Map<String, Object>> sources) { this.sources = sources; }
        public int getTokenUsed() { return tokenUsed; }
        public void setTokenUsed(int tokenUsed) { this.tokenUsed = tokenUsed; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    }
}
