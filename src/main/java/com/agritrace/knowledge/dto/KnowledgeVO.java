package com.agritrace.knowledge.dto;

import lombok.Data;

@Data
public class KnowledgeVO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private String source;
    private String tags;
    private Double score;
}
