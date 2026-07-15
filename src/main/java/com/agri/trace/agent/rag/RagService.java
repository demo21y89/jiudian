package com.agri.trace.agent.rag;

import com.agri.trace.entity.KnowledgeDoc;
import com.agri.trace.mapper.KnowledgeDocMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    @Autowired
    private KnowledgeDocMapper knowledgeDocMapper;

    @Autowired
    private RagConfig ragConfig;

    /**
     * 基于关键词的 RAG 检索（简化版，后续可接入向量数据库）
     */
    public List<Map<String, Object>> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 从知识库中检索相关文档
        List<KnowledgeDoc> allDocs = knowledgeDocMapper.selectList(null);

        // 2. 简单关键词匹配评分（生产环境应使用向量检索）
        String lowerQuery = query.toLowerCase();
        List<ScoredDoc> scoredDocs = new ArrayList<>();

        for (KnowledgeDoc doc : allDocs) {
            double score = 0;
            String content = doc.getContent() != null ? doc.getContent().toLowerCase() : "";
            String title = doc.getTitle() != null ? doc.getTitle().toLowerCase() : "";

            // 关键词匹配
            String[] keywords = lowerQuery.split("\\s+");
            for (String kw : keywords) {
                if (kw.length() < 2) continue;
                if (content.contains(kw)) score += 2;
                if (title.contains(kw)) score += 3;
            }

            // 品类匹配
            String[] categories = {"水果", "蔬菜", "粮食", "茶叶", "畜禽", "苹果", "大米", "猪肉"};
            for (String cat : categories) {
                if (lowerQuery.contains(cat) && (content.contains(cat) || title.contains(cat))) {
                    score += 5;
                }
            }

            if (score > 0) {
                scoredDocs.add(new ScoredDoc(doc, score));
            }
        }

        // 3. 按分数排序，取 Top-K
        scoredDocs.sort((a, b) -> Double.compare(b.score, a.score));
        return scoredDocs.stream()
                .limit(ragConfig.getTopK())
                .map(sd -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", sd.doc.getId());
                    item.put("title", sd.doc.getTitle());
                    item.put("category", sd.doc.getCategory());
                    item.put("content", truncate(sd.doc.getContent(), 200));
                    item.put("score", sd.score);
                    return item;
                })
                .collect(Collectors.toList());
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() <= maxLen ? str : str.substring(0, maxLen) + "...";
    }

    private static class ScoredDoc {
        final KnowledgeDoc doc;
        final double score;

        ScoredDoc(KnowledgeDoc doc, double score) {
            this.doc = doc;
            this.score = score;
        }
    }
}
