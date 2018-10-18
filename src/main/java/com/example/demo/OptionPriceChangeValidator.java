package com.example.demo;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

import static com.example.demo.OptionPriceChangeViolation.REQUEST_MUST_BE_NOT_NULL;

/**
 * Created by yangwansu on 2018. 10. 18..
 */
public class OptionPriceChangeValidator {
    public List<OptionPriceChangeViolation> validate(OptionPriceChangeRequest request) {
        if (request == null) {
            return Lists.newArrayList(REQUEST_MUST_BE_NOT_NULL);
        }
        return Collections.EMPTY_LIST;
    }
}
