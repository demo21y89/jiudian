package com.agri.trace.controller.admin;

import com.agri.trace.dto.R;
import com.agri.trace.entity.KnowledgeDoc;
import com.agri.trace.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/knowledge")
public class AdminKnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;

    @GetMapping("/list")
    public R<List<KnowledgeDoc>> list() {
        return R.ok(knowledgeService.list());
    }

    @PostMapping
    public R<?> save(@RequestBody KnowledgeDoc doc) {
        knowledgeService.save(doc);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        knowledgeService.delete(id);
        return R.ok();
    }

    @GetMapping("/search")
    public R<List<KnowledgeDoc>> search(@RequestParam String keyword) {
        return R.ok(knowledgeService.search(keyword));
    }

    @GetMapping("/category/{category}")
    public R<List<KnowledgeDoc>> byCategory(@PathVariable String category) {
        return R.ok(knowledgeService.findByCategory(category));
    }
}
