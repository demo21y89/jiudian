package com.agritrace.knowledge.repository;

import com.agritrace.knowledge.entity.KnowledgeDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgeDocRepository extends JpaRepository<KnowledgeDoc, Long> {

    List<KnowledgeDoc> findByCategory(String category);

    @Query(value = "SELECT * FROM knowledge_docs WHERE enabled = true AND to_tsvector('chinese', content) @@ to_tsquery('chinese', :query)", nativeQuery = true)
    List<KnowledgeDoc> fullTextSearch(@Param("query") String query);
}
