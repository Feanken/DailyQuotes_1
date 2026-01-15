package com.example.dailyquotes_1.ui;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.dailyquotes_1.R;
import com.example.dailyquotes_1.worker.DailyQuoteWorker;
import com.example.dailyquotes_1.util.PreferencesManager;

import java.util.concurrent.TimeUnit;

/**
 * SettingsActivity allows users to configure application settings.
 * Current features:
 * - Toggle for daily quote notifications
 * - Integration with SharedPreferences for persistence
 * - WorkManager integration for scheduling background tasks
 */
public class SettingsActivity extends AppCompatActivity {

    private Switch notificationSwitch;
    private static final String DAILY_QUOTE_WORK_TAG = "daily_quote_work";

    /**
     * Called when the activity is first created.
     * Initializes UI components and loads saved preferences.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state, if available
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize PreferencesManager on first use
        PreferencesManager.init(this);

        // Initialize UI components
        notificationSwitch = findViewById(R.id.switch_notifications);

        // Load saved notification preference
        boolean notificationsEnabled = PreferencesManager.isNotificationEnabled();
        notificationSwitch.setChecked(notificationsEnabled);

        // Set up switch listener for toggle events
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onNotificationToggled(isChecked);
            }
        });
    }

    /**
     * Handles notification toggle change.
     * Schedules or cancels the WorkManager task based on the toggle state.
     *
     * @param isEnabled true if notifications should be enabled, false otherwise
     */
    private void onNotificationToggled(boolean isEnabled) {
        // Save preference
        PreferencesManager.setNotificationEnabled(isEnabled);

        if (isEnabled) {
            // Schedule daily quote worker
            scheduleDailyQuoteWorker();
        } else {
            // Cancel daily quote worker
            cancelDailyQuoteWorker();
        }
    }

    /**
     * Schedules the daily quote worker to run periodically.
     * The worker fetches a random quote and displays a notification.
     * Interval: 15 minutes (can be changed)
     */
    private void scheduleDailyQuoteWorker() {
        // Create a periodic work request for the DailyQuoteWorker
        // Runs every 15 minutes
        PeriodicWorkRequest dailyQuoteWork =
                new PeriodicWorkRequest.Builder(
                        DailyQuoteWorker.class,
                        15,
                        TimeUnit.MINUTES
                )
                .addTag(DAILY_QUOTE_WORK_TAG)
                .build();

        // Enqueue the work with KEEP policy (keep existing work)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                DAILY_QUOTE_WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyQuoteWork
        );
    }

    /**
     * Cancels all scheduled daily quote work.
     * Called when user disables notifications.
     */
    private void cancelDailyQuoteWorker() {
        WorkManager.getInstance(this).cancelAllWorkByTag(DAILY_QUOTE_WORK_TAG);
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
}
