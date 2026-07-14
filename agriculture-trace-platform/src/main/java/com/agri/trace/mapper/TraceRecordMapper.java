package com.agri.trace.mapper;

import com.agri.trace.entity.TraceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface TraceRecordMapper extends BaseMapperExt<TraceRecord> {

    @Select("SELECT * FROM trace_record WHERE batch_id = #{batchId} ORDER BY record_time ASC")
    List<TraceRecord> findByBatchId(@Param("batchId") Long batchId);

    @Select("SELECT * FROM trace_record WHERE batch_id = #{batchId} AND record_type = #{type}")
    List<TraceRecord> findByBatchIdAndType(@Param("batchId") Long batchId, @Param("type") String type);
}
