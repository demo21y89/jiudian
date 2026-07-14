package com.agri.trace.service;

import com.agri.trace.entity.KnowledgeDoc;
import java.util.List;

public interface KnowledgeService {
    List<KnowledgeDoc> search(String keyword);
    List<KnowledgeDoc> findByCategory(String category);
    boolean save(KnowledgeDoc doc);
    boolean delete(Long id);
    List<KnowledgeDoc> list();
}
