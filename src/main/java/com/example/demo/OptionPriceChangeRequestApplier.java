package com.example.demo;


import org.apache.commons.collections.CollectionUtils;

import java.util.*;

import static com.example.demo.RequestFailReasons.*;

public class OptionPriceChangeRequestApplier {
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final RequestHistoryRepository requestHistoryRepository;
    private final RequestFailHandler requestFailHandler;

    public OptionPriceChangeRequestApplier(ProductRepository productRepository, OptionRepository optionRepository, RequestHistoryRepository requestHistoryRepository, RequestFailHandler requestFailHandler) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
        this.requestHistoryRepository = requestHistoryRepository;
        this.requestFailHandler = requestFailHandler;
    }

    public boolean save(OptionPriceChangeRequest request) {

        Product product = productRepository.findById(request.getProductId());

        if (product == null) {
            requestFailHandler.handle(request, NOT_FOUND_PRODUCT);
            return false;
        }

        if (product.getStatus() != ProductStatus.LIVED) {
            requestFailHandler.handle(request, ILLEGAL_STATUS_PRODUCT);
            return false;
        }

        if (CollectionUtils.isEmpty(request.getOptions())) {
            requestFailHandler.handle(request, OPTION_IS_EMPTY);
            return false;
        }
        // 설계 자체에 대한 잘 잘못을 가리기도 힘듬 어디서 부터 어떻게 시작해야하나 ?
        //추가적인 요구사항
        //애써 만들어 놓고 깨기 싫어함

        Map<Long, RequestOption> map = new HashMap<>();
        List<Long> requestedOptionIds = new ArrayList<>();
        for (RequestOption requestOption : request.getOptions()) {
            map.put(requestOption.getOptionId(), requestOption);
            requestedOptionIds.add(requestOption.getOptionId());
        }

        List<Option> options = optionRepository.findByOptionIdIn(requestedOptionIds);
        Map<Long, Option> existingOptionMap = new HashMap<>();
        for (Option option : options) {
            existingOptionMap.put(option.getId(), option);
        }

        List<Option> qualifiedOptions = new ArrayList<>();
        List<Long> unqualifiedOptionIds = new ArrayList<>();
        for (Long requestedOptionId : requestedOptionIds) {
            if (existingOptionMap.get(requestedOptionId) != null) {
                Option option = existingOptionMap.get(requestedOptionId);
                if (option.getProductId().equals(product.getId())) {
                    if (option.getPrice() != map.get(option.getId()).getBefore()) {
                        unqualifiedOptionIds.add(option.getId());
                        requestFailHandler.handle(request, requestedOptionId, ILLEGAL_BEFORE_VALUE);
                    } else {
                        qualifiedOptions.add(option);
                    }
                } else {
                    unqualifiedOptionIds.add(option.getId());
                    requestFailHandler.handle(request, requestedOptionId, ILLEGAL_OPTION_PRODUCT_ID);
                }
            } else {
                unqualifiedOptionIds.add(requestedOptionId);
                requestFailHandler.handle(request, requestedOptionId, NOT_FOUND_OPTION);
            }
        }

        if (CollectionUtils.isEmpty(qualifiedOptions)) {
            return false;
        }

        Iterator<RequestOption> it = request.getOptions().iterator();
        while (it.hasNext()) {
            RequestOption requestOption = it.next();
            if (unqualifiedOptionIds.contains(requestOption.getOptionId())) {
                it.remove();
            }
        }

        product.setStatus(ProductStatus.REQUEST);
        productRepository.save(product);
        for (Option option : qualifiedOptions) {
            RequestOption requestedOption = map.get(option.getId());
            option.setPrice(requestedOption.getAfter());
            optionRepository.save(option);
        }

        requestHistoryRepository.save(request);

        return true;
    }
}
