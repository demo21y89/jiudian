package com.agritrace.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AIConfig {
    /** API地址（兼容OpenAI格式） */
    private String baseUrl = "https://api.openai.com/v1";
    /** API密钥 */
    private String apiKey = "";
    /** 模型名称 */
    private String model = "gpt-4o-mini";
    /** 最大Token */
    private int maxTokens = 2048;
    /** 温度 */
    private double temperature = 0.3;
    /** 是否启用AI */
    private boolean enabled = false;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
