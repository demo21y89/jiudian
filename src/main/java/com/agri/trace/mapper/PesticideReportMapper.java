package com.agri.trace.mapper;

import com.agri.trace.entity.PesticideReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface PesticideReportMapper extends BaseMapperExt<PesticideReport> {

    @Select("SELECT * FROM pesticide_report WHERE batch_id = #{batchId}")
    List<PesticideReport> findByBatchId(@Param("batchId") Long batchId);

    @Select("SELECT * FROM pesticide_report WHERE batch_id = #{batchId} AND is_compliant = 0")
    List<PesticideReport> findNonCompliant(@Param("batchId") Long batchId);
}
