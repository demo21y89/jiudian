package com.agritrace.knowledge.dto;

import lombok.Data;

@Data
public class KnowledgeRetrieveRequest {
    private String query;
    private Integer topK = 5;
    private String category;
}
