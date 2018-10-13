package com.example.demo;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public class Product {
    private ProductStatus status;
    private Long id;

    public ProductStatus getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
