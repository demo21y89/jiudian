package com.agri.trace.agent.tools;

import com.agri.trace.entity.Batch;
import com.agri.trace.entity.PesticideReport;
import com.agri.trace.mapper.BatchMapper;
import com.agri.trace.mapper.PesticideReportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class PesticideAnalyzeTool implements McpTool {

    @Autowired
    private PesticideReportMapper pesticideReportMapper;

    @Autowired
    private BatchMapper batchMapper;

    @Override
    public String getName() {
        return "pesticide_analyze";
    }

    @Override
    public String getDescription() {
        return "解析农残检测报告，判断是否符合GB 2763标准，输入批次号，返回是否达标及超标详情";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String batchNo = (String) params.get("batchNo");
        Map<String, Object> result = new LinkedHashMap<>();

        if (batchNo == null) {
            result.put("error", "批次号不能为空");
            return result;
        }

        Batch batch = batchMapper.findByBatchNo(batchNo);
        if (batch == null) {
            result.put("error", "未找到该批次: " + batchNo);
            return result;
        }

        List<PesticideReport> reports = pesticideReportMapper.findByBatchId(batch.getId());
        if (reports.isEmpty()) {
            result.put("message", "该批次暂无农残检测报告");
            result.put("compliant", null);
            return result;
        }

        boolean allCompliant = true;
        List<Map<String, Object>> details = new ArrayList<>();
        for (PesticideReport r : reports) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("itemName", r.getItemName());
            item.put("result", r.getResult());
            item.put("standardLimit", r.getStandardLimit());
            item.put("unit", r.getUnit());
            item.put("compliant", r.getIsCompliant());
            if (Boolean.FALSE.equals(r.getIsCompliant())) {
                allCompliant = false;
            }
            details.add(item);
        }

        result.put("compliant", allCompliant);
        result.put("standard", "GB 2763-2021 食品中农药最大残留限量");
        result.put("batchNo", batchNo);
        result.put("details", details);

        return result;
    }
}
