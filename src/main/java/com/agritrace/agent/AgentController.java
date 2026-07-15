package com.agritrace.agent;

import com.agritrace.agent.memory.ConversationMemory;
import com.agritrace.ai.AIService;
import com.agritrace.common.response.ApiResult;
import com.agritrace.knowledge.dto.KnowledgeRetrieveRequest;
import com.agritrace.knowledge.dto.KnowledgeVO;
import com.agritrace.knowledge.service.KnowledgeService;
import com.agritrace.mcp.core.MCPDispatcher;
import com.agritrace.mcp.core.MCPResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {

    private static final Logger log = LoggerFactory.getLogger(AgentController.class);

    private final MCPDispatcher mcpDispatcher;
    private final KnowledgeService knowledgeService;
    private final AIService aiService;
    private final Map<String, ConversationMemory> sessions = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private static final String SYSTEM_PROMPT = """
        你是AgriTrace农产品溯源平台的AI智能助手。你的能力包括：
        1. 溯源查询——查询农产品从种植到销售的全链路信息
        2. 农残分析——解析农药残留检测报告
        3. 物流追踪——查询订单物流状态
        4. 法规检索——查询食品安全法规和种植标准
        5. 证书验证——验证有机认证和合格证真伪
        6. 智能推荐——根据用户偏好推荐优质农产品

        请用中文回答，语气专业且亲切。如果用户问题超出你的能力范围，请礼貌说明。
        """;

    public AgentController(MCPDispatcher mcpDispatcher, KnowledgeService knowledgeService, AIService aiService) {
        this.mcpDispatcher = mcpDispatcher;
        this.knowledgeService = knowledgeService;
        this.aiService = aiService;
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestBody Map<String, Object> request) {
        String query = (String) request.getOrDefault("query", "");
        String sessionId = (String) request.getOrDefault("session_id", UUID.randomUUID().toString().substring(0, 8));
        Long userId = request.get("user_id") != null ? Long.valueOf(request.get("user_id").toString()) : 1L;

        SseEmitter emitter = new SseEmitter(120_000L);
        ConversationMemory memory = sessions.computeIfAbsent(sessionId, ConversationMemory::new);

        executor.execute(() -> {
            try {
                memory.addMessage("user", query);
                long startTime = System.currentTimeMillis();

                // 1. 意图识别：AI优先，规则兜底
                String intent = classifyIntentByRule(query);
                if (intent == null) intent = aiService.classifyIntent(query);
                if (intent == null) intent = "general";
                sendEvent(emitter, "tool", "意图识别: " + intentToLabel(intent));
                log.info("Session[{}] intent: {} (query: {})", sessionId, intent, query);

                // 2. 执行MCP工具调用
                List<String> toolsUsed = new ArrayList<>();
                StringBuilder toolResult = new StringBuilder();
                switch (intent) {
                    case "trace_query" -> { toolsUsed.add("trace_query"); toolResult = handleToolForSSE(query, "trace_query", emitter, "溯源查询"); }
                    case "pesticide" -> { toolsUsed.add("pesticide_analysis"); toolResult = handleToolForSSE(query, "pesticide_analysis", emitter, "农残检测"); }
                    case "logistics" -> { toolsUsed.add("logistics_query"); toolResult = handleToolForSSE(query, "logistics_query", emitter, "物流追踪"); }
                    case "regulation" -> { toolsUsed.add("regulation_query"); toolResult = handleToolForSSE(query, "regulation_query", emitter, "法规检索"); }
                    case "cert_verify" -> { toolsUsed.add("cert_verification"); toolResult = handleToolForSSE(query, "cert_verification", emitter, "证书验证"); }
                    case "recommend" -> { toolsUsed.add("recommend"); toolResult = handleToolForSSE(query, "recommend", emitter, "智能推荐"); }
                    default -> toolResult = handleKnowledgeSearch(query, emitter);
                }

                // 3. AI生成自然语言回复
                String aiReply = aiService.generateResponse(SYSTEM_PROMPT,
                    "用户问题：" + query + "\n\n系统查询结果：" + toolResult.toString() + "\n\n请根据以上查询结果，用自然语言回答用户的问题。");
                
                if (aiReply != null && !aiReply.isEmpty()) {
                    sendEvent(emitter, "content", aiReply);
                } else if (toolResult.length() > 0) {
                    sendEvent(emitter, "content", toolResult.toString());
                } else {
                    sendEvent(emitter, "content", "你好！我是AgriTrace AI智能助手。请输入您想了解的问题。");
                }

                if (!toolsUsed.isEmpty()) {
                    sendEvent(emitter, "tool", "已调用: " + String.join("、", toolsUsed));
                }

                long execTime = System.currentTimeMillis() - startTime;
                sendEvent(emitter, "content", "\n\n⏱ 响应耗时: " + execTime + "ms");

                memory.addMessage("assistant", "[AI回复]");
                emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                emitter.complete();

            } catch (Exception e) {
                log.error("Agent SSE异常: {}", e.getMessage());
                try { sendEvent(emitter, "error", "服务异常: " + e.getMessage()); emitter.complete(); }
                catch (IOException ex) { emitter.completeWithError(ex); }
            }
        });

        emitter.onTimeout(emitter::complete);
        emitter.onError(e -> emitter.complete());
        return emitter;
    }

    @PostMapping("/chat")
    public ApiResult<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String query = (String) request.getOrDefault("query", "");
        String sessionId = (String) request.getOrDefault("session_id", "sess_default");
        long start = System.currentTimeMillis();

        ConversationMemory memory = sessions.computeIfAbsent(sessionId, ConversationMemory::new);
        memory.addMessage("user", query);

        String intent = aiService.classifyIntent(query);
        if (intent == null) intent = classifyIntentByRule(query);

        List<String> tools = new ArrayList<>();
        StringBuilder toolResult = new StringBuilder();

        switch (intent) {
            case "trace_query": tools.add("trace_query"); toolResult.append(formatMcpResult(mcpDispatcher.invoke("trace_query", buildParams(query, "trace_query")))); break;
            case "pesticide": tools.add("pesticide_analysis"); toolResult.append(formatMcpResult(mcpDispatcher.invoke("pesticide_analysis", buildParams(query, "pesticide_analysis")))); break;
            case "logistics": tools.add("logistics_query"); toolResult.append(formatMcpResult(mcpDispatcher.invoke("logistics_query", buildParams(query, "logistics_query")))); break;
            case "regulation": tools.add("regulation_query"); toolResult.append(formatMcpResult(mcpDispatcher.invoke("regulation_query", buildParams(query, "regulation_query")))); break;
            case "cert_verify": tools.add("cert_verification"); toolResult.append(formatMcpResult(mcpDispatcher.invoke("cert_verification", buildParams(query, "cert_verification")))); break;
            case "recommend": tools.add("recommend"); toolResult.append(formatMcpResult(mcpDispatcher.invoke("recommend", buildParams(query, "recommend")))); break;
            default: toolResult = searchKnowledge(query); break;
        }

        String aiReply = aiService.generateResponse(SYSTEM_PROMPT, "用户问题：" + query + "\n\n系统查询结果：" + toolResult + "\n\n请根据以上查询结果，用自然语言回答。");
        String answer = (aiReply != null && !aiReply.isEmpty()) ? aiReply : (toolResult.length() > 0 ? toolResult.toString() : "请输入您想了解的问题。");

        memory.addMessage("assistant", answer);

        Map<String, Object> result = new HashMap<>();
        result.put("content", answer);
        result.put("sessionId", sessionId);
        result.put("toolsUsed", tools);
        result.put("executionTime", System.currentTimeMillis() - start);
        return ApiResult.success(result);
    }

    @GetMapping("/skills")
    public ApiResult<List<Map<String, Object>>> skills() {
        return ApiResult.success(mcpDispatcher.listAvailableSkills());
    }

    // ═══ 规则意图识别（AI未配置时的兜底） ═══
    private String classifyIntentByRule(String query) {
        String lower = query.toLowerCase();
        if (lower.contains("溯源") || lower.contains("追溯") || lower.contains("批次") || lower.contains("生产") || lower.contains("来源") || lower.contains("哪来")) return "trace_query";
        if (lower.contains("农残") || lower.contains("农药") || lower.contains("残留") || lower.contains("检测") || lower.contains("达标") || lower.contains("合格")) return "pesticide";
        if (lower.contains("物流") || lower.contains("运输") || lower.contains("快递") || lower.contains("到哪") || lower.contains("配送") || lower.contains("发货")) return "logistics";
        if (lower.contains("合格证") || lower.contains("认证") || lower.contains("证书") || lower.contains("验证") || lower.contains("有机")) return "cert_verify";
        if (lower.contains("推荐") || lower.contains("优质") || lower.contains("好吃") || lower.contains("买什么") || lower.contains("好物")) return "recommend";
        if (lower.contains("法规") || lower.contains("法律") || lower.contains("条例") || lower.contains("标准") || lower.contains("规定") || lower.contains("政策")) return "regulation";
        return "general";
    }

    private String intentToLabel(String intent) {
        return switch (intent) {
            case "trace_query" -> "溯源查询";
            case "pesticide" -> "农残分析";
            case "logistics" -> "物流追踪";
            case "cert_verify" -> "证书验证";
            case "recommend" -> "智能推荐";
            case "regulation" -> "法规检索";
            default -> "通用问答";
        };
    }

    // ═══ SSE流式工具调用 ═══
    private StringBuilder handleToolForSSE(String query, String skill, SseEmitter emitter, String label) throws IOException {
        MCPResult res = mcpDispatcher.invoke(skill, buildParams(query, skill));
        StringBuilder sb = new StringBuilder();
        if ("success".equals(res.getStatus()) && res.getData() != null) {
            sendEvent(emitter, "source", label);
            if (res.getData() instanceof Map data) {
                for (Object key : data.keySet()) {
                    Object val = data.get(key);
                    if (val instanceof List list && !list.isEmpty()) {
                        sb.append("【").append(key).append("】").append(list.size()).append("条\n");
                        for (Object item : list) {
                            if (item instanceof Map m) {
                                for (Object mk : m.keySet()) sb.append("  ").append(mk).append(": ").append(m.get(mk)).append("\n");
                                sb.append("\n");
                            } else sb.append("  - ").append(item).append("\n");
                        }
                    } else if (val != null) {
                        sb.append("【").append(key).append("】").append(val).append("\n");
                    }
                }
            } else sb.append(res.getData().toString()).append("\n");
        } else {
            String err = "暂无数据";
            if (res.getData() instanceof Map m && m.containsKey("error")) err = m.get("error").toString();
            sb.append("查询结果：").append(err).append("\n");
        }
        return sb;
    }

    private StringBuilder handleKnowledgeSearch(String query, SseEmitter emitter) throws IOException {
        KnowledgeRetrieveRequest req = new KnowledgeRetrieveRequest();
        req.setQuery(query); req.setTopK(3);
        List<KnowledgeVO> results = knowledgeService.retrieve(req);
        StringBuilder sb = new StringBuilder();
        if (!results.isEmpty()) {
            sendEvent(emitter, "source", "知识库命中" + results.size() + "条");
            sb.append("知识库检索结果：\n");
            for (KnowledgeVO vo : results) {
                sb.append("《").append(vo.getTitle()).append("》").append(vo.getContent()).append("\n");
            }
        }
        return sb;
    }

    private StringBuilder searchKnowledge(String query) {
        KnowledgeRetrieveRequest req = new KnowledgeRetrieveRequest();
        req.setQuery(query); req.setTopK(3);
        List<KnowledgeVO> results = knowledgeService.retrieve(req);
        StringBuilder sb = new StringBuilder();
        if (!results.isEmpty()) {
            results.forEach(vo -> sb.append("《").append(vo.getTitle()).append("》").append(vo.getContent()).append("\n"));
        } else sb.append("暂无相关知识。");
        return sb;
    }

    // ═══ MCP工具参数构建 ═══
    private Map<String, Object> buildParams(String query, String skill) {
        Map<String, Object> params = new HashMap<>();
        String code = extractPattern(query, "(TRC\\w+|B\\d{8,})");
        switch (skill) {
            case "trace_query":
                if (!code.isEmpty()) { params.put("trace_code", code); params.put("batch_no", code); }
                break;
            case "pesticide_analysis":
                if (!code.isEmpty()) params.put("batch_no", code);
                params.put("product_name", extractProduct(query));
                break;
            case "logistics_query":
                params.put("order_no", extractPattern(query, "ORD\\w+"));
                break;
            case "regulation_query": params.put("keyword", query); break;
            case "cert_verification": params.put("query", query); break;
            case "recommend":
                String cat = query.contains("水果")||query.contains("苹果")?"水果":query.contains("蔬菜")?"蔬菜":query.contains("谷物")||query.contains("大米")?"谷物":"";
                params.put("category", cat); params.put("limit", 5);
                break;
        }
        return params;
    }

    private String formatMcpResult(MCPResult res) {
        if ("success".equals(res.getStatus()) && res.getData() != null) return res.getData().toString();
        if (res.getData() instanceof Map m && m.containsKey("error")) return m.get("error").toString();
        return "暂无数据";
    }

    private String extractPattern(String query, String pattern) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(query);
        return m.find() ? m.group() : "";
    }

    private String extractProduct(String query) {
        if (query.contains("苹果")) return "苹果";
        if (query.contains("番茄")||query.contains("西红柿")) return "番茄";
        if (query.contains("大米")) return "大米";
        if (query.contains("芒果")) return "芒果";
        return "";
    }

    private void sendEvent(SseEmitter emitter, String type, String data) throws IOException {
        try { emitter.send(SseEmitter.event().name(type).data(Map.of("type", type, "data", data))); }
        catch (IOException e) { log.warn("SSE发送失败: {}", e.getMessage()); }
    }
}
