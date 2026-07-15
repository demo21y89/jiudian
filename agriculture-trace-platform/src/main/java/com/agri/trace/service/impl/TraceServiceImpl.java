package com.agri.trace.service.impl;

import com.agri.trace.entity.Batch;
import com.agri.trace.entity.PesticideReport;
import com.agri.trace.entity.TraceRecord;
import com.agri.trace.mapper.BatchMapper;
import com.agri.trace.mapper.PesticideReportMapper;
import com.agri.trace.mapper.TraceRecordMapper;
import com.agri.trace.service.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TraceServiceImpl implements TraceService {

    @Autowired
    private BatchMapper batchMapper;

    @Autowired
    private TraceRecordMapper traceRecordMapper;

    @Autowired
    private PesticideReportMapper pesticideReportMapper;

    @Override
    public Batch getBatchByNo(String batchNo) {
        return batchMapper.findByBatchNo(batchNo);
    }

    @Override
    public List<TraceRecord> getTraceRecords(Long batchId) {
        return traceRecordMapper.findByBatchId(batchId);
    }

    @Override
    public List<PesticideReport> getPesticideReports(Long batchId) {
        return pesticideReportMapper.findByBatchId(batchId);
    }

    @Override
    public Map<String, Object> getFullTraceInfo(String batchNo) {
        Map<String, Object> result = new LinkedHashMap<>();
        Batch batch = batchMapper.findByBatchNo(batchNo);
        if (batch == null) {
            result.put("error", "未找到该批次的溯源信息");
            return result;
        }

        // Batch 转 Map
        Map<String, Object> batchMap = new LinkedHashMap<>();
        batchMap.put("id", batch.getId());
        batchMap.put("batchNo", batch.getBatchNo());
        batchMap.put("productId", batch.getProductId());
        batchMap.put("produceDate", batch.getProduceDate() != null ? batch.getProduceDate().toString() : null);
        batchMap.put("harvestDate", batch.getHarvestDate() != null ? batch.getHarvestDate().toString() : null);
        batchMap.put("quantity", batch.getQuantity());
        batchMap.put("farmAddress", batch.getFarmAddress());
        batchMap.put("farmArea", batch.getFarmArea());
        batchMap.put("soilType", batch.getSoilType());
        batchMap.put("createTime", batch.getCreateTime() != null ? batch.getCreateTime().toString() : null);
        result.put("batch", batchMap);

        // 溯源记录转 Map
        List<TraceRecord> records = traceRecordMapper.findByBatchId(batch.getId());
        List<Map<String, Object>> recordList = new ArrayList<>();
        for (TraceRecord r : records) {
            Map<String, Object> rm = new LinkedHashMap<>();
            rm.put("id", r.getId());
            rm.put("recordType", r.getRecordType());
            rm.put("recordTime", r.getRecordTime() != null ? r.getRecordTime().toString() : null);
            rm.put("operator", r.getOperator());
            rm.put("content", r.getContent());
            rm.put("detail", r.getDetail());
            recordList.add(rm);
        }
        result.put("traceRecords", recordList);

        // 农残报告转 Map
        List<PesticideReport> reports = pesticideReportMapper.findByBatchId(batch.getId());
        List<Map<String, Object>> reportList = new ArrayList<>();
        for (PesticideReport r : reports) {
            Map<String, Object> pm = new LinkedHashMap<>();
            pm.put("id", r.getId());
            pm.put("itemName", r.getItemName());
            pm.put("result", r.getResult());
            pm.put("standardLimit", r.getStandardLimit());
            pm.put("unit", r.getUnit());
            pm.put("isCompliant", r.getIsCompliant());
            pm.put("testDate", r.getTestDate() != null ? r.getTestDate().toString() : null);
            pm.put("testOrganization", r.getTestOrganization());
            reportList.add(pm);
        }
        result.put("pesticideReports", reportList);
        result.put("overallCompliant", reports.stream().allMatch(r -> r.getIsCompliant() != null && r.getIsCompliant()));

        return result;
    }

    @Override
    @Transactional
    public boolean addTraceRecord(TraceRecord record) {
        record.setCreateTime(LocalDateTime.now());
        return traceRecordMapper.insert(record) > 0;
    }

    @Override
    @Transactional
    public boolean addPesticideReport(PesticideReport report) {
        report.setCreateTime(LocalDateTime.now());
        return pesticideReportMapper.insert(report) > 0;
    }

    @Override
    @Transactional
    public boolean createBatch(Batch batch) {
        batch.setCreateTime(LocalDateTime.now());
        return batchMapper.insert(batch) > 0;
    }
}
