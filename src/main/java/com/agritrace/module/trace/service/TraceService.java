package com.agritrace.module.trace.service;

import com.agritrace.common.exception.BusinessException;
import com.agritrace.module.trace.dto.TraceQueryRequest;
import com.agritrace.module.trace.dto.TraceVO;
import com.agritrace.module.trace.entity.TraceRecord;
import com.agritrace.module.trace.entity.TraceStage;
import com.agritrace.module.trace.repository.TraceRepository;
import com.agritrace.module.trace.repository.TraceStageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TraceService {

    private final TraceRepository traceRepository;
    private final TraceStageRepository stageRepository;

    public TraceService(TraceRepository traceRepository, TraceStageRepository stageRepository) {
        this.traceRepository = traceRepository;
        this.stageRepository = stageRepository;
    }

    @Transactional
    public TraceVO queryTrace(TraceQueryRequest request) {
        String code = request.getTraceCode();
        String batch = request.getBatchNo();

        TraceRecord record = null;
        if (code != null && !code.isEmpty()) {
            record = traceRepository.findByTraceCode(code).orElse(null);
        }
        if (record == null && batch != null && !batch.isEmpty()) {
            record = traceRepository.findByBatchNo(batch).orElse(null);
        }
        if (record == null) {
            throw new BusinessException("未找到该溯源记录，请确认溯源码或批次号正确。示例：TRC20260714001");
        }

        // 增加扫码次数
        record.setScanCount(record.getScanCount() + 1);
        traceRepository.save(record);

        return toVO(record);
    }

    public TraceVO toVO(TraceRecord record) {
        TraceVO vo = new TraceVO();
        vo.setTraceCode(record.getTraceCode());
        vo.setBatchNo(record.getBatchNo());
        vo.setProductName(record.getProductName());
        vo.setOrigin(record.getOrigin());
        vo.setCertNo(record.getCertNo());
        vo.setCertValid("valid".equals(record.getCertStatus()) || record.getCertStatus() == null);
        vo.setScanCount(record.getScanCount());
        vo.setTxHash(record.getTxHash());

        // 加载溯源环节
        List<TraceStage> stages = stageRepository.findByTraceIdOrderBySortOrderAsc(record.getId());
        List<Map<String, Object>> stageList = stages.stream().map(s -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("stage", s.getStageName());
            m.put("stageName", s.getStageName());
            m.put("time", s.getStageTime() != null ? s.getStageTime().toString() : "");
            m.put("recordTime", s.getStageTime() != null ? s.getStageTime().toString() : "");
            m.put("description", s.getDescription() != null ? s.getDescription() : "");
            m.put("operator", s.getOperator() != null ? s.getOperator() : "");
            m.put("location", s.getLocation() != null ? s.getLocation() : "");
            return m;
        }).collect(Collectors.toList());
        vo.setStages(stageList);

        return vo;
    }

    public List<TraceVO> listAll() {
        return traceRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }
}
