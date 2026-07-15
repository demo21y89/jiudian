package com.agri.trace.agent.rag;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.rag")
public class RagConfig {
    private int topK = 5;
    private double similarityThreshold = 0.7;
}
