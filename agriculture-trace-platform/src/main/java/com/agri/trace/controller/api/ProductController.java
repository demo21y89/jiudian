package com.agri.trace.controller.api;

import com.agri.trace.dto.R;
import com.agri.trace.entity.Product;
import com.agri.trace.service.ProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public R<IPage<Product>> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String category) {
        return R.ok(productService.page(page, size, category));
    }

    @GetMapping("/{id}")
    public R<Product> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return product != null ? R.ok(product) : R.error("商品不存在");
    }

    @GetMapping("/search")
    public R<List<Product>> search(@RequestParam String keyword) {
        return R.ok(productService.search(keyword));
    }

    @GetMapping("/batch/{batchNo}")
    public R<Product> findByBatchNo(@PathVariable String batchNo) {
        Product product = productService.findByBatchNo(batchNo);
        return product != null ? R.ok(product) : R.error("未找到该批次商品");
    }
}
