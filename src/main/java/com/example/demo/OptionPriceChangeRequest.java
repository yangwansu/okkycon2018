package com.example.demo;

import java.util.List;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public class OptionPriceChangeRequest {

    private Long productId;
    private Long transactionId;
    private List<RequestOption> options;

    public Long getProductId() {
        return productId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public List<RequestOption> getOptions() {
        return options;
    }

    public OptionPriceChangeRequest setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public OptionPriceChangeRequest setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public OptionPriceChangeRequest setOptions(List<RequestOption> options) {
        this.options = options;
        return this;
    }
}
