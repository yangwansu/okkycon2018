package com.example.demo;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public interface RequestFailHandler {

    void handle(OptionPriceChangeRequest request, RequestFailReasons fail);
    void handle(OptionPriceChangeRequest request, long optionId, RequestFailReasons fail);
}
