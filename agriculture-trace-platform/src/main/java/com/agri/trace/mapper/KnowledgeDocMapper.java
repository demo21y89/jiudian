package com.agri.trace.mapper;

import com.agri.trace.entity.KnowledgeDoc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface KnowledgeDocMapper extends BaseMapperExt<KnowledgeDoc> {

    @Select("SELECT * FROM knowledge_doc WHERE category = #{category}")
    List<KnowledgeDoc> findByCategory(@Param("category") String category);

    @Select("SELECT * FROM knowledge_doc WHERE content LIKE '%' || #{keyword} || '%'")
    List<KnowledgeDoc> search(@Param("keyword") String keyword);
}
