package com.agri.trace.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatResponse {
    private String answer;
    private String sessionId;
    private List<String> sources;
}
