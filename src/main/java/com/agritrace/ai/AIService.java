package com.agritrace.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI大模型服务——调用OpenAI兼容API
 */
@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    private final AIConfig config;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public AIService(AIConfig config) {
        this.config = config;
    }

    /**
     * 发送对话请求，返回AI回复文本
     */
    public String chat(String systemPrompt, String userMessage) {
        if (!config.isEnabled() || config.getApiKey().isEmpty()) {
            log.debug("AI未启用，返回默认回复");
            return null;
        }
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userMessage));

            Map<String, Object> body = Map.of(
                "model", config.getModel(),
                "messages", messages,
                "max_tokens", config.getMaxTokens(),
                "temperature", config.getTemperature()
            );

            String json = mapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Map<String, Object> result = mapper.readValue(response.body(), Map.class);
                List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> msg = (Map<String, Object>) choices.get(0).get("message");
                    return (String) msg.get("content");
                }
            } else {
                log.warn("AI API返回错误: {} {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("AI API调用失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 意图识别：让AI判断用户意图并返回JSON
     */
    public String classifyIntent(String userMessage) {
        if (!config.isEnabled() || config.getApiKey().isEmpty()) {
            return null;
        }
        String prompt = """
            你是农产品溯源平台的意图识别器。根据用户输入，返回以下意图之一（只返回单词）：
            trace_query - 溯源查询（查产品来源、生产记录、批次信息）
            pesticide - 农残分析（问农药残留、检测报告、达标情况）
            logistics - 物流查询（问运输、快递、配送状态）
            cert_verify - 证书验证（问合格证、有机认证、承诺达标）
            recommend - 智能推荐（求推荐、买什么好）
            regulation - 法规检索（问法律法规、标准、政策）
            general - 通用问答（以上都不是）

            用户输入：%s
            意图：""".formatted(userMessage);
        String result = chat(prompt, userMessage);
        if (result != null) {
            result = result.trim().toLowerCase();
            if (result.contains("trace")) return "trace_query";
            if (result.contains("pesticide") || result.contains("农残")) return "pesticide";
            if (result.contains("logistics") || result.contains("物流")) return "logistics";
            if (result.contains("cert") || result.contains("证书")) return "cert_verify";
            if (result.contains("recommend") || result.contains("推荐")) return "recommend";
            if (result.contains("regulation") || result.contains("法规")) return "regulation";
        }
        return null;
    }

    /**
     * 生成自然语言回复
     */
    public String generateResponse(String systemPrompt, String userMessage) {
        return chat(systemPrompt, userMessage);
    }
}
