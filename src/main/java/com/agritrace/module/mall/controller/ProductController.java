package com.agritrace.module.mall.controller;

import com.agritrace.common.response.ApiResult;
import com.agritrace.common.response.PageResult;
import com.agritrace.module.mall.dto.ProductCreateRequest;
import com.agritrace.module.mall.dto.ProductQueryParams;
import com.agritrace.module.mall.dto.ProductVO;
import com.agritrace.module.mall.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ApiResult<ProductVO> create(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResult.success(productService.createProduct(request));
    }

    @GetMapping("/{id}")
    public ApiResult<ProductVO> getById(@PathVariable Long id) {
        return ApiResult.success(productService.getProductById(id));
    }

    @GetMapping
    public ApiResult<PageResult<java.util.List<ProductVO>>> list(ProductQueryParams params) {
        return ApiResult.success(productService.listProducts(params));
    }
}


