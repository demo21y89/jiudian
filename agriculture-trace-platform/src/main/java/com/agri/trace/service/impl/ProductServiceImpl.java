package com.agri.trace.service.impl;

import com.agri.trace.entity.Product;
import com.agri.trace.mapper.ProductMapper;
import com.agri.trace.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public IPage<Product> page(int page, int size, String category) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .eq(category != null && !category.isEmpty(), Product::getCategory, category)
                .orderByDesc(Product::getCreateTime);
        return productMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public List<Product> search(String keyword) {
        return productMapper.search(keyword);
    }

    @Override
    public Product findByBatchNo(String batchNo) {
        return productMapper.findByBatchNo(batchNo);
    }

    @Override
    public boolean save(Product product) {
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        return productMapper.insert(product) > 0;
    }

    @Override
    public boolean update(Product product) {
        product.setUpdateTime(LocalDateTime.now());
        return productMapper.updateById(product) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return productMapper.deleteById(id) > 0;
    }

    @Override
    public List<Product> list() {
        return productMapper.selectList(null);
    }
}
