package com.example.demo;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public interface ProductRepository {
    Product findById(Long productId);
    void save(Product product);
}
