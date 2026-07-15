package com.agri.trace.service.impl;

import com.agri.trace.entity.KnowledgeDoc;
import com.agri.trace.mapper.KnowledgeDocMapper;
import com.agri.trace.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private KnowledgeDocMapper knowledgeDocMapper;

    @Override
    public List<KnowledgeDoc> search(String keyword) {
        return knowledgeDocMapper.search(keyword);
    }

    @Override
    public List<KnowledgeDoc> findByCategory(String category) {
        return knowledgeDocMapper.findByCategory(category);
    }

    @Override
    public boolean save(KnowledgeDoc doc) {
        doc.setUploadTime(LocalDateTime.now());
        return knowledgeDocMapper.insert(doc) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return knowledgeDocMapper.deleteById(id) > 0;
    }

    @Override
    public List<KnowledgeDoc> list() {
        return knowledgeDocMapper.selectList(null);
    }
}
