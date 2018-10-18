package com.example.demo;

import org.junit.Test;

import java.util.List;

import static com.example.demo.OptionPriceChangeViolation.REQUEST_MUST_BE_NOT_NULL;
import static org.assertj.core.api.Assertions.assertThat;

public class OptionPriceChangeValidatorTest {


    @Test
    public void REQUEST_MUST_BE_NOT_NULL() {
        OptionPriceChangeValidator validator = new OptionPriceChangeValidator();

        List<OptionPriceChangeViolation> violations = validator.validate(null);

        assertThat(violations).contains(REQUEST_MUST_BE_NOT_NULL);
    }

}