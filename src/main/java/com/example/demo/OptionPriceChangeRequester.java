package com.example.demo;


/**
 * Created by yangwansu on 2018. 10. 9..
 */
public class OptionPriceChangeRequester {

    private final OptionPriceChangeRequestApplier optionPriceChangeRequestApplier;
    private final RequestSuccessHandler requestSuccessHandler;
    private final RequestFailHandler requestFailHandler;

    public OptionPriceChangeRequester(OptionPriceChangeRequestApplier optionPriceChangeRequestApplier, RequestSuccessHandler requestSuccessHandler, RequestFailHandler requestFailHandler) {
        this.optionPriceChangeRequestApplier = optionPriceChangeRequestApplier;
        this.requestSuccessHandler = requestSuccessHandler;
        this.requestFailHandler = requestFailHandler;
    }

    public boolean send(OptionPriceChangeRequest request) {
        OptionPriceChangeResponse response = optionPriceChangeRequestApplier.save(request);
        if (response.hasFails()) {
            requestFailHandler.handle(request, response);
            return false;
        }
        requestSuccessHandler.handle(request);
        return true;

    }

}
