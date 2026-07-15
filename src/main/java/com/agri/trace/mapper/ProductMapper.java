package com.agri.trace.mapper;

import com.agri.trace.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapperExt<Product> {

    @Select("SELECT * FROM product WHERE category = #{category} AND status = 1 AND deleted = 0")
    List<Product> findByCategory(@Param("category") String category);

    @Select("SELECT * FROM product WHERE status = 1 AND deleted = 0 AND (name LIKE '%' || #{keyword} || '%' OR category LIKE '%' || #{keyword} || '%' OR origin LIKE '%' || #{keyword} || '%' OR description LIKE '%' || #{keyword} || '%')")
    List<Product> search(@Param("keyword") String keyword);

    @Select("SELECT * FROM product WHERE batch_no = #{batchNo}")
    Product findByBatchNo(@Param("batchNo") String batchNo);
}