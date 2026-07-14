package com.agritrace.knowledge.controller;

import com.agritrace.common.response.ApiResult;
import com.agritrace.common.response.PageResult;
import com.agritrace.knowledge.dto.KnowledgeRetrieveRequest;
import com.agritrace.knowledge.dto.KnowledgeVO;
import com.agritrace.knowledge.service.KnowledgeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @PostMapping("/api/v1/rag/retrieve")
    public ApiResult<List<KnowledgeVO>> retrieve(@Valid @RequestBody KnowledgeRetrieveRequest request) {
        return ApiResult.success(knowledgeService.retrieve(request));
    }

    @GetMapping("/api/v1/knowledge")
    public ApiResult<PageResult<List<KnowledgeVO>>> listAll() {
        List<KnowledgeVO> docs = knowledgeService.listAll();
        return ApiResult.success(new PageResult<>(1, docs.size(), docs.size(), docs));
    }
}
