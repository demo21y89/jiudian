package com.agritrace.module.trace.service;

import com.agritrace.common.exception.BusinessException;
import com.agritrace.module.trace.dto.TraceQueryRequest;
import com.agritrace.module.trace.dto.TraceVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class TraceService {

    private final JdbcTemplate jdbcTemplate;
    private boolean tableChecked = false;
    private boolean tableExists = false;

    public TraceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("unchecked")
    public TraceVO queryTrace(TraceQueryRequest request) {
        String code = request.getTraceCode();
        if (code == null || code.isEmpty()) {
            throw new BusinessException("溯源码不能为空");
        }

        // Try database first if table exists
        if (ensureTableExists()) {
            try {
                Map<String, Object> row = jdbcTemplate.queryForMap(
                    "SELECT * FROM trace_records WHERE trace_code = ?", code);
                return mapToVO(row, code);
            } catch (Exception ignored) {}
        }

        // Return demo data for known codes
        if ("TRC20260714001".equals(code)) {
            return createDemoTrace(code, "B20260701", "红富士苹果", "山东省烟台市",
                "CQ20260701001", true, 23, "0x7a3f9c2b8e1d5f4a",
                Arrays.asList(
                    map("stage","种植","time","2026-03-15","data","春季修剪、施肥"),
                    map("stage","开花","time","2026-04-10","data","苹果花盛开"),
                    map("stage","疏果","time","2026-05-20","data","人工疏果、套袋"),
                    map("stage","采收","time","2026-07-01","data","成熟采收"),
                    map("stage","检测","time","2026-07-08","data","农残检测全部合格")
                ));
        }
        if ("TRC20260713002".equals(code)) {
            return createDemoTrace(code, "B20260702", "五常大米", "黑龙江省五常市",
                "CQ20260702001", true, 15, "0x8b4d1e2f3a6c7b9d",
                Arrays.asList(
                    map("stage","育苗","time","2026-03-01","data","大棚育苗"),
                    map("stage","插秧","time","2026-04-20","data","机械插秧"),
                    map("stage","田间管理","time","2026-05-15","data","有机施肥、人工除草"),
                    map("stage","收割","time","2026-07-10","data","机械收割"),
                    map("stage","加工","time","2026-07-15","data","脱壳、碾米、色选"),
                    map("stage","检测","time","2026-07-18","data","重金属检测合格")
                ));
        }
        throw new BusinessException("未找到该溯源码对应的记录，请确认输入正确。可用示例：TRC20260714001");
    }

    private boolean ensureTableExists() {
        if (tableChecked) return tableExists;
        try {
            jdbcTemplate.execute("SELECT COUNT(*) FROM trace_records");
            tableExists = true;
        } catch (Exception e) {
            try {
                jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS trace_records (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                    "trace_code VARCHAR(50) NOT NULL UNIQUE," +
                    "batch_no VARCHAR(50)," +
                    "product_name VARCHAR(200)," +
                    "origin VARCHAR(200)," +
                    "cert_no VARCHAR(50)," +
                    "cert_valid BOOLEAN DEFAULT FALSE," +
                    "stages TEXT," +
                    "tx_hash VARCHAR(100)," +
                    "scan_count INT DEFAULT 0," +
                    "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                tableExists = true;
            } catch (Exception ex) {
                tableExists = false;
            }
        }
        tableChecked = true;
        return tableExists;
    }

    private TraceVO createDemoTrace(String code, String batch, String product, String origin,
                                     String certNo, boolean certValid, int scans, String txHash,
                                     List<Map<String,String>> stages) {
        TraceVO vo = new TraceVO();
        vo.setTraceCode(code);
        vo.setBatchNo(batch);
        vo.setProductName(product);
        vo.setOrigin(origin);
        vo.setCertNo(certNo);
        vo.setCertValid(certValid);
        vo.setScanCount(scans);
        vo.setTxHash(txHash);
        vo.setStages(stages);
        return vo;
    }

    private TraceVO mapToVO(Map<String, Object> row, String code) {
        TraceVO vo = new TraceVO();
        vo.setTraceCode((String) row.get("trace_code"));
        vo.setBatchNo((String) row.get("batch_no"));
        vo.setProductName((String) row.get("product_name"));
        vo.setOrigin((String) row.get("origin"));
        vo.setCertNo((String) row.get("cert_no"));
        Object cv = row.get("cert_valid");
        vo.setCertValid(cv instanceof Boolean ? (Boolean)cv : false);
        vo.setScanCount(row.get("scan_count") instanceof Number ? ((Number)row.get("scan_count")).intValue() : 0);
        vo.setTxHash((String) row.get("tx_hash"));
        String stagesStr = (String) row.get("stages");
        if (stagesStr != null && !stagesStr.isEmpty()) {
            try {
                vo.setStages(new com.fasterxml.jackson.databind.ObjectMapper().readTree(stagesStr));
            } catch (Exception ex) {
                vo.setStages(stagesStr);
            }
        }
        return vo;
    }

    private Map<String, String> map(String... vals) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < vals.length; i += 2) m.put(vals[i], vals[i+1]);
        return m;
    }
}