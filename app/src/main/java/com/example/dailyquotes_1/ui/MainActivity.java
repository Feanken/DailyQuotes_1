package com.example.dailyquotes_1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dailyquotes_1.R;

/**
 * MainActivity is the entry point of the application.
 * It displays a dashboard with navigation buttons to access different features:
 * - Quotes: Browse quotes from the Quotable API
 * - Notes: Manage personal notes with timestamps
 * - Settings: Configure notification preferences
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Initializes UI components and sets up navigation button listeners.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state, if available
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize and set up navigation buttons
        setupNavigationButtons();
    }

    /**
     * Sets up click listeners for all navigation buttons.
     * Launches corresponding activities when buttons are clicked.
     */
    private void setupNavigationButtons() {
        // Quotes Button - navigates to QuotesActivity
        Button quotesButton = findViewById(R.id.btn_quotes);
        quotesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuotesActivity.class);
            startActivity(intent);
        });

        // Notes Button - navigates to NotesActivity
        Button notesButton = findViewById(R.id.btn_notes);
        notesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotesActivity.class);
            startActivity(intent);
        });

        // Settings Button - navigates to SettingsActivity
        Button settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}
