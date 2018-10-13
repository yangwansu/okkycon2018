package com.example.demo;

/**
 * Created by yangwansu on 2018. 10. 9..
 */
public class RequestOption {

    private Long optionId;
    private long before;
    private long after;

    public RequestOption setOptionId(Long optionId) {
        this.optionId = optionId;
        return this;
    }

    public RequestOption setBefore(long before) {
        this.before = before;
        return this;
    }

    public RequestOption setAfter(long after) {
        this.after = after;
        return this;
    }

    public Long getOptionId() {
        return optionId;
    }

    public long getBefore() {
        return before;
    }
    public long getAfter() {
        return after;
    }
}
