package com.agritrace.knowledge.repository;

import com.agritrace.knowledge.entity.KnowledgeVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgeVectorRepository extends JpaRepository<KnowledgeVector, Long> {

    List<KnowledgeVector> findByCategory(String category);
}
