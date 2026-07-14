package com.agri.trace.service;

import com.agri.trace.entity.Batch;
import com.agri.trace.entity.PesticideReport;
import com.agri.trace.entity.TraceRecord;
import java.util.List;
import java.util.Map;

public interface TraceService {
    Batch getBatchByNo(String batchNo);
    List<TraceRecord> getTraceRecords(Long batchId);
    List<PesticideReport> getPesticideReports(Long batchId);
    Map<String, Object> getFullTraceInfo(String batchNo);
    boolean addTraceRecord(TraceRecord record);
    boolean addPesticideReport(PesticideReport report);
    boolean createBatch(Batch batch);
}
