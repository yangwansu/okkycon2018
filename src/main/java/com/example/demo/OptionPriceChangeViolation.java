package com.example.demo;

import lombok.EqualsAndHashCode;

/**
 * Created by yangwansu on 2018. 10. 16..
 */
public class OptionPriceChangeViolation {

    public static final OptionPriceChangeViolation OPTION_IS_EMPTY = new OptionPriceChangeViolation();
    public static final OptionPriceChangeViolation REQUEST_MUST_BE_NOT_NULL = new OptionPriceChangeViolation();
    public static final OptionPriceChangeViolation NOT_FOUND_PRODUCT = new OptionPriceChangeViolation();

    public static OptionPriceChangeViolation illegalStatusProduct(Long productId) {
        return new IllegalProductStatus(productId);
    }

    public static OptionPriceChangeViolation illegalBeforeValue(Long productId, Long optionId) {
        return new IllegalBeforeValue(productId, optionId);
    }

    public static OptionPriceChangeViolation notFoundOption(Long optionId) {
        return new NotFoundOption(optionId);
    }

    public static OptionPriceChangeViolation illegalOptionProductId() {
        return new IllegalOptionProductId();
    }


    public static class NotFoundOption extends OptionPriceChangeViolation {
        private final Long optionId;

        public NotFoundOption(Long optionId) {
            super();
            this.optionId = optionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            NotFoundOption that = (NotFoundOption) o;

            return new org.apache.commons.lang3.builder.EqualsBuilder()
                    .append(optionId, that.optionId)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new org.apache.commons.lang3.builder.HashCodeBuilder(17, 37)
                    .append(optionId)
                    .toHashCode();
        }
    }


    public static class IllegalProductStatus extends OptionPriceChangeViolation {
        public IllegalProductStatus(Long productId) {
            super();
        }
    }
    public static class IllegalBeforeValue extends OptionPriceChangeViolation {

        private final Long productId;
        private final Long optionId;

        public IllegalBeforeValue(Long productId, Long optionId) {

            this.productId = productId;
            this.optionId = optionId;
        }
    }

    private static class IllegalOptionProductId extends OptionPriceChangeViolation {

    }
}
