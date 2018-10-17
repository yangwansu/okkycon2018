package com.example.demo;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yangwansu on 2018. 10. 15..
 */
public class OptionPriceChangeResponse {

    private final List<OptionPriceChangeViolation> violations;

    public OptionPriceChangeResponse(List<OptionPriceChangeViolation> violations) {
        this.violations = violations;
    }

    public OptionPriceChangeResponse(OptionPriceChangeViolation ... violations) {
        this(Arrays.asList(violations));
    }

    public boolean hasFails() {
        return !getViolations().isEmpty();
    }

    public List<OptionPriceChangeViolation> getViolations() {
        return violations;
    }
}
