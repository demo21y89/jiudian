package com.agritrace.knowledge.service;

import com.agritrace.knowledge.dto.KnowledgeRetrieveRequest;
import com.agritrace.knowledge.dto.KnowledgeVO;
import com.agritrace.knowledge.entity.KnowledgeDoc;
import com.agritrace.knowledge.repository.KnowledgeDocRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeService {

    private final KnowledgeDocRepository docRepository;

    public KnowledgeService(KnowledgeDocRepository docRepository) {
        this.docRepository = docRepository;
    }

    public List<KnowledgeVO> retrieve(KnowledgeRetrieveRequest request) {
        List<KnowledgeDoc> docs;
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            docs = docRepository.findByCategory(request.getCategory());
        } else {
            docs = docRepository.findAll();
        }

        return docs.stream()
                .filter(d -> d.getEnabled())
                .map(doc -> {
                    KnowledgeVO vo = new KnowledgeVO();
                    vo.setId(doc.getId());
                    vo.setTitle(doc.getTitle());
                    vo.setContent(truncateContent(doc.getContent(), 200));
                    vo.setCategory(doc.getCategory());
                    vo.setSource(doc.getSource());
                    vo.setTags(doc.getTags());
                    vo.setScore(calculateRelevance(doc.getTitle(), doc.getContent(), request.getQuery()));
                    return vo;
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(request.getTopK())
                .collect(Collectors.toList());
    }

    private double calculateRelevance(String title, String content, String query) {
        String lowerQuery = query.toLowerCase();
        double score = 0;
        if (title.toLowerCase().contains(lowerQuery)) score += 0.5;
        if (content.toLowerCase().contains(lowerQuery)) score += 0.3;
        String[] keywords = lowerQuery.split("\\s+");
        for (String kw : keywords) {
            if (kw.length() > 1) {
                if (title.toLowerCase().contains(kw)) score += 0.1;
                if (content.toLowerCase().contains(kw)) score += 0.05;
            }
        }
        return Math.min(score, 1.0);
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) return "";
        return content.length() <= maxLength ? content : content.substring(0, maxLength) + "...";
    }
}
