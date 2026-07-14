package com.agri.trace.agent;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.model")
public class AiConfig {
    private String apiKey;
    private String modelName;
    private String baseUrl;
    /** 供应商: openai / deepseek / dashscope(通义千问) / custom */
    private String provider = "openai";
}
