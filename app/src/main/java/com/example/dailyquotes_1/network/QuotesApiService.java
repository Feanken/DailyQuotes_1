package com.example.dailyquotes_1.network;

import com.example.dailyquotes_1.model.Quote;
import com.example.dailyquotes_1.model.QuoteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit service interface for the DummyJSON Quotes API.
 * Defines endpoints for fetching quotes with Callbacks (Java style).
 *
 * Base URL: https://dummyjson.com/
 */
public interface QuotesApiService {

    /**
     * Fetches a list of quotes with pagination support.
     *
     * @param limit Maximum number of quotes to return (default 10)
     * @param skip Number of quotes to skip for pagination (default 0)
     * @return Call object wrapping QuoteResponse for callback-based execution
     */
        @GET("quotes")
        Call<QuoteResponse> getQuotes(
            @Query("limit") int limit,
            @Query("skip") int skip
        );

    /**
     * Fetches a single random quote.
     *
     * @return Call object wrapping Quote for callback-based execution
     */
    @GET("quotes/random")
    Call<Quote> getRandomQuote();
}
