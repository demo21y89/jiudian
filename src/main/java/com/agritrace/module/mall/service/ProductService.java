package com.agritrace.module.mall.service;

import com.agritrace.common.exception.BusinessException;
import com.agritrace.common.response.PageResult;
import com.agritrace.module.mall.dto.ProductCreateRequest;
import com.agritrace.module.mall.dto.ProductQueryParams;
import com.agritrace.module.mall.dto.ProductVO;
import com.agritrace.module.mall.entity.Product;
import com.agritrace.module.mall.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductVO createProduct(ProductCreateRequest request) {
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        if (product.getUnit() == null) product.setUnit("斤");
        product.setPublished(true);
        product = productRepository.save(product);
        return toProductVO(product);
    }

    public ProductVO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("商品不存在"));
        return toProductVO(product);
    }

    public PageResult<List<ProductVO>> listProducts(ProductQueryParams params) {
        PageRequest pageRequest = PageRequest.of(params.getPage() - 1, params.getSize());
        Page<Product> page = productRepository.findByPublishedTrue(pageRequest);
        Page<ProductVO> voPage = page.map(this::toProductVO);
        return new PageResult<>(params.getPage(), params.getSize(), voPage.getTotalElements(), voPage.getContent());
    }

    public ProductVO toProductVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        if (vo.getUnit() == null) vo.setUnit("斤");
        return vo;
    }
}