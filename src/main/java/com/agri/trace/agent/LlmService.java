package com.agri.trace.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmService {

    private static final Logger log = LoggerFactory.getLogger(LlmService.class);

    @Autowired
    private AiConfig aiConfig;

    @Autowired
    private ObjectMapper mapper;  // 使用 Spring 配置好的 ObjectMapper（含 JSR310 支持）

    public String chat(String systemPrompt, String userMessage, List<Map<String, Object>> toolResults) {
        try {
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);

            StringBuilder enhancedMessage = new StringBuilder(userMessage);
            if (toolResults != null && !toolResults.isEmpty()) {
                enhancedMessage.append("\n\n【系统查询到的数据】\n");
                for (int i = 0; i < toolResults.size(); i++) {
                    try {
                        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(toolResults.get(i));
                        enhancedMessage.append("工具 ").append(i + 1).append(" 返回:\n").append(json).append("\n\n");
                    } catch (Exception e) {
                        log.warn("序列化工具结果失败: {}", e.getMessage());
                        enhancedMessage.append("工具 ").append(i + 1).append(" 返回: [数据序列化异常]\n\n");
                    }
                }
                enhancedMessage.append("请基于以上查询到的数据回答用户的问题。如果数据不足以回答，如实告知用户。");
            }

            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", enhancedMessage.toString());
            messages.add(userMsg);

            String url = getApiUrl();
            String requestBody = buildRequestBody(messages);

            log.info("调用 LLM API: provider={}, model={}, url={}", aiConfig.getProvider(), aiConfig.getModelName(), url);

            String responseJson = httpPost(url, requestBody);
            return parseResponse(responseJson);

        } catch (Exception e) {
            log.error("LLM API 调用失败", e);
            return "⚠️ AI 联网服务暂时不可用（" + e.getMessage() + "），请检查 API Key 配置。\n\n当前在离线模式下使用内置规则回答。";
        }
    }

    private String getApiUrl() {
        String provider = aiConfig.getProvider();
        String baseUrl = aiConfig.getBaseUrl();

        if (baseUrl != null && !baseUrl.isEmpty() && !"https://api.openai.com".equals(baseUrl)) {
            String url = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            if (url.endsWith("/chat/completions")) return url;
            if (url.endsWith("/v1")) return url + "/chat/completions";
            return url + "/v1/chat/completions";
        }

        switch (provider) {
            case "deepseek":
                return "https://api.deepseek.com/v1/chat/completions";
            case "dashscope":
                return "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
            default:
                return "https://api.openai.com/v1/chat/completions";
        }
    }

    private String buildRequestBody(List<Map<String, String>> messages) throws Exception {
        String provider = aiConfig.getProvider();

        if ("dashscope".equals(provider)) {
            ObjectNode root = mapper.createObjectNode();
            root.put("model", aiConfig.getModelName() != null ? aiConfig.getModelName() : "qwen-turbo");
            ObjectNode input = mapper.createObjectNode();
            StringBuilder prompt = new StringBuilder();
            for (Map<String, String> msg : messages) {
                prompt.append(msg.get("role")).append(": ").append(msg.get("content")).append("\n");
            }
            input.put("prompt", prompt.toString());
            root.set("input", input);
            ObjectNode params = mapper.createObjectNode();
            params.put("top_p", 0.8);
            params.put("temperature", 0.7);
            params.put("result_format", "message");
            root.set("parameters", params);
            return mapper.writeValueAsString(root);
        } else {
            ObjectNode root = mapper.createObjectNode();
            root.put("model", aiConfig.getModelName() != null ? aiConfig.getModelName() : "gpt-4o-mini");
            ArrayNode msgArray = mapper.createArrayNode();
            for (Map<String, String> msg : messages) {
                ObjectNode m = mapper.createObjectNode();
                m.put("role", msg.get("role"));
                m.put("content", msg.get("content"));
                msgArray.add(m);
            }
            root.set("messages", msgArray);
            root.put("temperature", 0.7);
            root.put("max_tokens", 2000);
            root.put("top_p", 0.8);
            return mapper.writeValueAsString(root);
        }
    }

    private String parseResponse(String responseJson) throws Exception {
        JsonNode root = mapper.readTree(responseJson);
        String provider = aiConfig.getProvider();

        if ("dashscope".equals(provider)) {
            JsonNode output = root.path("output");
            JsonNode choices = output.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
            return output.path("text").asText("无返回结果");
        } else {
            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                return choices.get(0).path("message").path("content").asText();
            }
            return root.path("text").asText("无返回结果");
        }
    }

    private String httpPost(String urlStr, String body) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);
        conn.setDoOutput(true);

        String apiKey = aiConfig.getApiKey();
        // 与 hasValidApiKey() 保持一致的校验：排除空值、未解析占位符、占位key
        String lowerKey = (apiKey != null) ? apiKey.toLowerCase() : null;
        if (apiKey != null && !apiKey.isEmpty()
                && !apiKey.startsWith("${")
                && lowerKey != null
                && !lowerKey.contains("placeholder")
                && !lowerKey.contains("your-api-key")
                && !lowerKey.contains("your_api_key")
                && !lowerKey.startsWith("sk-")
                && apiKey.length() >= 10) {
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        }

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                        StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        if (responseCode >= 400) {
            throw new RuntimeException("API 请求失败 [HTTP " + responseCode + "]: " + response);
        }

        return response.toString();
    }
}