package com.agri.trace.service;

import com.agri.trace.entity.Product;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;

public interface ProductService {
    IPage<Product> page(int page, int size, String category);
    Product getById(Long id);
    List<Product> search(String keyword);
    Product findByBatchNo(String batchNo);
    boolean save(Product product);
    boolean update(Product product);
    boolean delete(Long id);
    List<Product> list();
}
