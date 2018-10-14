package com.example.demo;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

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

    public Option option(Long productId, Long optionId) {
        return getOptions().stream().filter(it-> it.getProductId().equals(productId) && it.getId().equals(optionId)).findFirst().orElse(null);
    }

    static ProductEnv initRequest(long productId, ProductStatus status, Function<Long,Option> ... foptions) {
        return new ProductEnv(
                product(productId,status),
                Arrays.stream(foptions).map(f -> f.apply(productId)).collect(toList()));
    }

    static Product product(Long productId, ProductStatus status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        return product;
    }

    static Function<Long, Option> option(Long optionId, int price) {
        return (productId) -> option(productId, optionId, price);
    }

    static Option option(Long productId, Long optionId, int price) {
        Option option1 = new Option();
        option1.setId(optionId);
        option1.setProductId(productId);
        option1.setPrice(price);
        return option1;
    }
    public OptionPriceChangeRequestBuilder initRequest() {
        return OptionPriceChangeRequestBuilder.init(this);
    }
}
