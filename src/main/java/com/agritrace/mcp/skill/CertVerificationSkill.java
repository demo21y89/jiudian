package com.agritrace.mcp.skill;

import com.agritrace.mcp.spi.MCPPlugin;
import com.agritrace.module.trace.entity.TraceRecord;
import com.agritrace.module.trace.repository.TraceRepository;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CertVerificationSkill implements MCPPlugin {

    private final TraceRepository traceRepository;

    public CertVerificationSkill(TraceRepository traceRepository) {
        this.traceRepository = traceRepository;
    }

    @Override
    public String getSkillName() {
        return "cert_verification";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String certNo = (String) params.getOrDefault("cert_no", "");
        String traceCode = (String) params.getOrDefault("trace_code", "");

        Optional<TraceRecord> recordOpt = Optional.empty();
        if (!certNo.isEmpty()) {
            recordOpt = traceRepository.findByBatchNo(certNo.replace("CQ", "B"));
        } else if (!traceCode.isEmpty()) {
            recordOpt = traceRepository.findByTraceCode(traceCode);
        }

        if (recordOpt.isEmpty()) {
            return Map.of("valid", false, "message", "未找到对应的合格证记录");
        }

        TraceRecord record = recordOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("cert_no", record.getCertNo());
        response.put("valid", "VALID".equals(record.getCertStatus()));
        response.put("status", record.getCertStatus());
        response.put("product_name", record.getProductName());
        response.put("origin", record.getOrigin());
        response.put("issuance_date", record.getCreateTime());
        response.put("tx_hash", record.getTxHash());
        return response;
    }

    @Override
    public String getDescription() {
        return "合格证验真：验证电子承诺达标合格证的有效性";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
