package com.agri.trace.controller.admin;

import com.agri.trace.dto.R;
import com.agri.trace.entity.Product;
import com.agri.trace.service.ProductService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    public R<IPage<Product>> page(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String category) {
        return R.ok(productService.page(page, size, category));
    }

    @PostMapping
    public R<?> save(@RequestBody Product product) {
        productService.save(product);
        return R.ok();
    }

    @PutMapping
    public R<?> update(@RequestBody Product product) {
        productService.update(product);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return R.ok();
    }

    @GetMapping("/list")
    public R<?> list() {
        return R.ok(productService.list());
    }
}
