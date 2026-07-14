package com.agritrace.module.trace.repository;

import com.agritrace.module.trace.entity.TraceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TraceRepository extends JpaRepository<TraceRecord, Long> {
    Optional<TraceRecord> findByTraceCode(String traceCode);
    Optional<TraceRecord> findByBatchNo(String batchNo);
}
