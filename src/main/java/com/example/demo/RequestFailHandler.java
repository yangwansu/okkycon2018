package com.example.demo;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public interface RequestFailHandler {

    void handle(OptionPriceChangeRequest request, OptionPriceChangeResponse response);
}
