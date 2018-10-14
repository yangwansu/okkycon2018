package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yangwansu on 2018. 10. 14..
 */
public class OptionPriceChangeRequestBuilder {

    private ProductEnv productEnv;
    private List<Change> changes = new ArrayList<>();

    public static OptionPriceChangeRequestBuilder init(ProductEnv productEnv) {
        return new OptionPriceChangeRequestBuilder(productEnv);
    }

    public OptionPriceChangeRequestBuilder(ProductEnv productEnv) {
        this.productEnv = productEnv;
    }

    public OptionPriceChangeRequestBuilder changePrice(Long optionId, long after) {
        changes.add(new Change(optionId, after));
        return this;
    }

    public OptionPriceChangeRequest build() {
        List<RequestOption> aaa = changes.stream().map(it -> change(it.optionId, productEnv.option(productEnv.getProduct().getId(), it.optionId).getPrice(), it.changePrice)).collect(Collectors.toList());
        return request(productEnv.getProduct().getId(), aaa);
    }

    private OptionPriceChangeRequest request(Long productId, List<RequestOption> requestOption) {
        OptionPriceChangeRequest request = new OptionPriceChangeRequest();
        request.setProductId(productId);
        request.setTransactionId(1L);

        request.setOptions(requestOption);
        return request;
    }

    private RequestOption change(Long optionId, long before, long after) {
        RequestOption requestOption2 = new RequestOption();
        requestOption2.setOptionId(optionId);
        requestOption2.setBefore(before);
        requestOption2.setAfter(after);
        return requestOption2;
    }

    public static class Change {
        public final Long optionId;
        public final long changePrice;

        public Change(Long optionId, long changePrice) {
            this.optionId = optionId;
            this.changePrice = changePrice;
        }
    }

}
