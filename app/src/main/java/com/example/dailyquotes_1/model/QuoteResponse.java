package com.example.dailyquotes_1.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * POJO class representing the response wrapper from the DummyJSON API.
 * Example: /quotes?limit=10&skip=0 returns { quotes: [...], total, skip, limit }
 */
public class QuoteResponse {

    @SerializedName("quotes")
    private List<Quote> quotes;

    @SerializedName("total")
    private int total;

    @SerializedName("skip")
    private int skip;

    @SerializedName("limit")
    private int limit;

    /**
     * Default constructor required for Gson deserialization.
     */
    public QuoteResponse() {
    }

    // Getters and Setters

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }

    public int getSkip() {
        return skip;
    }

    public void setSkip(int skip) {
        this.skip = skip;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "QuoteResponse{" +
            "total=" + total +
            ", skip=" + skip +
            ", limit=" + limit +
            '}';
    }
}
