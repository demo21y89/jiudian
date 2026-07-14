package com.agri.trace.mapper;

import com.agri.trace.entity.Batch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BatchMapper extends BaseMapperExt<Batch> {

    @Select("SELECT * FROM batch WHERE batch_no = #{batchNo}")
    Batch findByBatchNo(@Param("batchNo") String batchNo);
}
