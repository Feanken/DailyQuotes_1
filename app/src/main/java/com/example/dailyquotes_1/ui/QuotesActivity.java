package com.example.dailyquotes_1.ui;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyquotes_1.R;
import com.example.dailyquotes_1.model.QuoteResponse;
import com.example.dailyquotes_1.network.RetrofitClient;
import com.example.dailyquotes_1.ui.adapter.QuotesAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * QuotesActivity displays a paginated list of quotes from the Quotable API.
 * Features:
 * - RecyclerView with custom adapter for displaying quotes
 * - Pagination support with scroll listener for auto-loading
 * - Loading indicator (ProgressBar) during API calls
 * - Error handling with Toast messages
 */
public class QuotesActivity extends AppCompatActivity {

    private RecyclerView quotesRecyclerView;
    private ProgressBar loadingProgressBar;
    private QuotesAdapter quotesAdapter;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean hasMoreQuotes = true;

    /**
     * Called when the activity is first created.
     * Initializes the RecyclerView, adapter, and loads the first page of quotes.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state, if available
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        quotesRecyclerView = findViewById(R.id.quotes_recycler_view);
        loadingProgressBar = findViewById(R.id.loading_progress_bar);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        quotesRecyclerView.setLayoutManager(layoutManager);

        // Create and set adapter
        quotesAdapter = new QuotesAdapter();
        quotesRecyclerView.setAdapter(quotesAdapter);

        // Set up pagination scroll listener
        setupScrollListener(layoutManager);

        // Load first page of quotes
        loadQuotes();
    }

    /**
     * Sets up a scroll listener to auto-load the next page when user scrolls near the end.
     * Triggers loading when the last 3 items are visible.
     *
     * @param layoutManager LinearLayoutManager for checking visible item positions
     */
    private void setupScrollListener(LinearLayoutManager layoutManager) {
        quotesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if we should load more quotes
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = quotesAdapter.getItemCount();

                // Load next page when within 3 items of the end
                if (!isLoading && hasMoreQuotes &&
                        lastVisibleItemPosition >= totalItemCount - 3) {
                    currentPage++;
                    loadQuotes();
                }
            }
        });
    }

    /**
     * Loads quotes from the API for the current page.
     * Uses Retrofit callback for asynchronous API call handling.
     * Updates the adapter and shows/hides loading indicator.
     */
    private void loadQuotes() {
        isLoading = true;
        loadingProgressBar.setVisibility(View.VISIBLE);

        // Calculate skip offset for pagination
        int skip = currentPage * PAGE_SIZE;

        // Abort early if offline to avoid pointless retries and page drift
        if (!isOnline()) {
            loadingProgressBar.setVisibility(View.GONE);
            isLoading = false;
            if (currentPage > 0) {
                currentPage--; // rollback page increment from scroll trigger
            }
            Toast.makeText(this, "Tidak ada koneksi internet. Periksa jaringan Anda.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make API call using Retrofit callback
        RetrofitClient.getQuotesApiService().getQuotes(PAGE_SIZE, skip)
            .enqueue(new Callback<QuoteResponse>() {
                    @Override
                    public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                        loadingProgressBar.setVisibility(View.GONE);
                        isLoading = false;

                        if (response.isSuccessful() && response.body() != null) {
                            QuoteResponse quoteResponse = response.body();

                            // Check if there are more quotes to load
                            if (quoteResponse.getQuotes() != null && !quoteResponse.getQuotes().isEmpty()) {
                                if (currentPage == 0) {
                                    // First page: set all quotes
                                    quotesAdapter.setQuotes(quoteResponse.getQuotes());
                                } else {
                                    // Subsequent pages: add quotes to existing list
                                    quotesAdapter.addQuotes(quoteResponse.getQuotes());
                                }

                                // Check if there are more quotes available
                                int totalLoaded = (currentPage + 1) * PAGE_SIZE;
                                if (totalLoaded >= quoteResponse.getTotal()) {
                                    hasMoreQuotes = false;
                                }
                            } else {
                                hasMoreQuotes = false;
                            }

                            Toast.makeText(QuotesActivity.this,
                                    "Loaded " + quoteResponse.getQuotes().size() + " quotes",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(QuotesActivity.this,
                                    "Failed to load quotes: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<QuoteResponse> call, Throwable t) {
                        loadingProgressBar.setVisibility(View.GONE);
                        isLoading = false;

                        Toast.makeText(QuotesActivity.this,
                                "Error loading quotes: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Handles the toolbar back button press.
     * Returns to the previous activity.
     *
     * @return true if the home button was pressed, false otherwise
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Checks network connectivity using ConnectivityManager.
     *
     * @return true if device has an active network with Internet capability
     */
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
