package com.example.demo;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public interface RequestFailHandler {
    void send(Long transactionId, Long productId, Long requestedOptionId);

    void send(OptionPriceChangeRequest request, RequestFailReasons fail);
    void send(OptionPriceChangeRequest request, long optionId, RequestFailReasons fail);
}
