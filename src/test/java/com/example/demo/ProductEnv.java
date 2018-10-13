package com.example.demo;

import java.util.List;

public class ProductEnv {

    private final Product product;
    private final List<Option> options;

    public ProductEnv(Product product, List<Option> options) {
        this.product = product;
        this.options = options;
    }


    public Product getProduct() {
        return product;
    }

    public List<Option> getOptions() {
        return options;
    }
}
