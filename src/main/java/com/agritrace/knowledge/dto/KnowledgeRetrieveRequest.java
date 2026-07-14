package com.agritrace.knowledge.dto;

import lombok.Data;

@Data
public class KnowledgeRetrieveRequest {
    private String query;
    private int topK = 5;
    private String category;
}
