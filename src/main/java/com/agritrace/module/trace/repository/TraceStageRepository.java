package com.agritrace.module.trace.repository;

import com.agritrace.module.trace.entity.TraceStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TraceStageRepository extends JpaRepository<TraceStage, Long> {
    List<TraceStage> findByTraceIdOrderBySortOrderAsc(Long traceId);
}
