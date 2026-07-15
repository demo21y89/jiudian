package com.agri.trace.controller.admin;

import com.agri.trace.dto.R;
import com.agri.trace.entity.Batch;
import com.agri.trace.entity.PesticideReport;
import com.agri.trace.entity.TraceRecord;
import com.agri.trace.service.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/trace")
public class AdminTraceController {

    @Autowired
    private TraceService traceService;

    @PostMapping("/batch")
    public R<?> createBatch(@RequestBody Batch batch) {
        traceService.createBatch(batch);
        return R.ok();
    }

    @GetMapping("/batch/{batchNo}")
    public R<Map<String, Object>> getTrace(@PathVariable String batchNo) {
        return R.ok(traceService.getFullTraceInfo(batchNo));
    }

    @PostMapping("/record")
    public R<?> addRecord(@RequestBody TraceRecord record) {
        traceService.addTraceRecord(record);
        return R.ok();
    }

    @PostMapping("/pesticide")
    public R<?> addPesticideReport(@RequestBody PesticideReport report) {
        traceService.addPesticideReport(report);
        return R.ok();
    }
}
