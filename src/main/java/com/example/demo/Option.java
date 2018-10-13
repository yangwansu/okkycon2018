package com.example.demo;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public class Option {
    private Long id;
    private Long productId;
    private long price;

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }



    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }


    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
