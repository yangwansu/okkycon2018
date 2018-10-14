package com.example.demo;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;

import java.util.*;

import static com.example.demo.RequestFailReasons.*;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public class OptionPriceChangeRequester {

    private final OptionPriceChangeRequestApplier optionPriceChangeRequestApplier;
    private final RequestSuccessHandler requestSuccessHandler;

    public OptionPriceChangeRequester(OptionPriceChangeRequestApplier optionPriceChangeRequestApplier, RequestSuccessHandler requestSuccessHandler) {
        this.optionPriceChangeRequestApplier = optionPriceChangeRequestApplier;
        this.requestSuccessHandler = requestSuccessHandler;
    }

    public boolean send(OptionPriceChangeRequest request) {
        if (optionPriceChangeRequestApplier.save(request)) {
            requestSuccessHandler.handle(request);
            return true;
        }

        return false;
    }

}
