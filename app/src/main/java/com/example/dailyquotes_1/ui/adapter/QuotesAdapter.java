package com.example.dailyquotes_1.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyquotes_1.R;
import com.example.dailyquotes_1.model.Quote;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying quotes.
 * Uses the ViewHolder pattern for efficient view reuse.
 * Supports pagination with setQuotes() for initial load and addQuotes() for loading more.
 */
public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {

    private List<Quote> quotes;

    /**
     * Constructor initializes the quote list.
     */
    public QuotesAdapter() {
        this.quotes = new ArrayList<>();
    }

    /**
     * Creates a new ViewHolder when RecyclerView needs one.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new QuoteViewHolder that holds the View for displaying a quote
     */
    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new QuoteViewHolder(inflater, parent);
    }

    /**
     * Binds the data to the ViewHolder for the quote at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at position
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quotes.get(position);
        holder.bind(quote);
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return The number of quotes in the list
     */
    @Override
    public int getItemCount() {
        return quotes.size();
    }

    /**
     * Sets the quotes list and notifies the adapter of the change.
     * Used for initial load of the first page.
     *
     * @param quoteList The new list of quotes to display
     */
    public void setQuotes(List<Quote> quoteList) {
        this.quotes = new ArrayList<>(quoteList);
        notifyDataSetChanged();
    }

    /**
     * Adds quotes to the existing list and notifies the adapter.
     * Used for pagination when loading more quotes.
     *
     * @param newQuotes The quotes to add to the list
     */
    public void addQuotes(List<Quote> newQuotes) {
        int startPosition = quotes.size();
        this.quotes.addAll(newQuotes);
        notifyItemRangeInserted(startPosition, newQuotes.size());
    }

    /**
     * Inner ViewHolder class for displaying individual quote items.
     */
    public static class QuoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView quoteTextView;
        private final TextView authorTextView;

        /**
         * Constructor initializes the views for displaying quote content and author.
         *
         * @param inflater LayoutInflater for inflating the item layout
         * @param parent The parent ViewGroup
         */
        public QuoteViewHolder(@NonNull LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_quote, parent, false));

            quoteTextView = itemView.findViewById(R.id.text_quote_content);
            authorTextView = itemView.findViewById(R.id.text_quote_author);
        }

        /**
         * Binds a quote to this ViewHolder's views.
         *
         * @param quote The quote data to display
         */
        public void bind(Quote quote) {
            quoteTextView.setText(quote.getContent());
            authorTextView.setText("— " + quote.getAuthor());
        }
    }
}
