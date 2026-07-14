package com.agritrace.knowledge.controller;

import com.agritrace.common.response.ApiResult;
import com.agritrace.knowledge.dto.KnowledgeRetrieveRequest;
import com.agritrace.knowledge.dto.KnowledgeVO;
import com.agritrace.knowledge.service.KnowledgeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rag")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @PostMapping("/retrieve")
    public ApiResult<List<KnowledgeVO>> retrieve(@Valid @RequestBody KnowledgeRetrieveRequest request) {
        return ApiResult.success(knowledgeService.retrieve(request));
    }
}
