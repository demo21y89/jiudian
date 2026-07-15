package com.agri.trace.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class ChatRequest {
    @NotBlank
    private String message;
    private String sessionId;
}
