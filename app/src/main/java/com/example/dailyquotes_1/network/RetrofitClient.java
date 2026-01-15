package com.example.dailyquotes_1.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton class for Retrofit client setup.
 * Initializes Retrofit with Quotable API base URL and Gson converter.
 * Uses double-checked locking pattern for thread-safe lazy initialization.
 */
public class RetrofitClient {

    private static final String BASE_URL = "https://dummyjson.com/";
    private static volatile Retrofit retrofit;
    private static volatile QuotesApiService quotesApiService;

    /**
     * Private constructor to prevent instantiation.
     */
    private RetrofitClient() {
    }

    /**
     * Gets or creates the Retrofit instance using double-checked locking.
     * This ensures thread safety and lazy initialization.
     *
     * @return Retrofit instance configured for Quotable API
     */
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    // Create Gson instance with lenient parsing for flexible JSON handling
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    // Build Retrofit with base URL and Gson converter
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofit;
    }

    /**
     * Gets or creates the QuotesApiService instance.
     * This service is used to make API calls to the Quotable endpoints.
     *
     * @return QuotesApiService instance for making API requests
     */
    public static QuotesApiService getQuotesApiService() {
        if (quotesApiService == null) {
            synchronized (RetrofitClient.class) {
                if (quotesApiService == null) {
                    quotesApiService = getRetrofitInstance().create(QuotesApiService.class);
                }
            }
        }
        return quotesApiService;
    }
}
