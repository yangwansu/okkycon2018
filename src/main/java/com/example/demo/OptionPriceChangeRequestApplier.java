package com.example.demo;


import org.apache.commons.collections.CollectionUtils;

import java.util.*;

import static com.example.demo.OptionPriceChangeViolation.*;

public class OptionPriceChangeRequestApplier {
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final RequestHistoryRepository requestHistoryRepository;

    public OptionPriceChangeRequestApplier(ProductRepository productRepository, OptionRepository optionRepository, RequestHistoryRepository requestHistoryRepository) {
        this.productRepository = productRepository;
        this.optionRepository = optionRepository;
        this.requestHistoryRepository = requestHistoryRepository;
    }

    public OptionPriceChangeResponse save(OptionPriceChangeRequest request) {
        if (request == null) {
            return new OptionPriceChangeResponse(OptionPriceChangeViolation.REQUEST_MUST_BE_NOT_NULL);
        }

        if (CollectionUtils.isEmpty(request.getOptions())) {
            return new OptionPriceChangeResponse(OPTION_IS_EMPTY);
        }

        Product product = productRepository.findById(request.getProductId());
        if (product == null) {
            return new OptionPriceChangeResponse(NOT_FOUND_PRODUCT);
        }

        if (product.getStatus() != ProductStatus.LIVED) {
            return new OptionPriceChangeResponse(OptionPriceChangeViolation.illegalStatusProduct(request.getProductId()));
        }


        List<OptionPriceChangeViolation> violations = new ArrayList<>();


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
                        violations.add(illegalBeforeValue(product.getId(), option.getId()));
                    } else {
                        qualifiedOptions.add(option);
                    }
                } else {
                    unqualifiedOptionIds.add(option.getId());
                    violations.add(illegalOptionProductId());
                }
            } else {
                unqualifiedOptionIds.add(requestedOptionId);
                violations.add(notFoundOption(requestedOptionId));
            }
        }

        OptionPriceChangeResponse response = new OptionPriceChangeResponse(violations);
        if (CollectionUtils.isEmpty(qualifiedOptions)) {
            return response;
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

        return response;
    }

}
