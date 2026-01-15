package com.example.dailyquotes_1.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dailyquotes_1.R;
import com.example.dailyquotes_1.model.Quote;
import com.example.dailyquotes_1.network.RetrofitClient;
import com.example.dailyquotes_1.ui.MainActivity;
import com.example.dailyquotes_1.util.PreferencesManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * WorkManager Worker for fetching daily quotes and displaying notifications.
 * This worker runs periodically (as configured in SettingsActivity)
 * and fetches a random quote from the Quotable API.
 * 
 * Uses Java's synchronous Retrofit callback (blocking) rather than async
 * since WorkManager automatically runs workers on a background thread.
 */
public class DailyQuoteWorker extends Worker {

    private static final String NOTIFICATION_CHANNEL_ID = "daily_quotes_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "Daily Quotes";
    private static final int NOTIFICATION_ID = 42;

    /**
     * Constructor required by WorkManager.
     *
     * @param context The application context
     * @param params Parameters for the work
     */
    public DailyQuoteWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * The work method called by WorkManager.
     * Fetches a random quote and displays a notification.
     * Returns success if notification is shown, or retry on failure.
     *
     * @return Result indicating success or retry
     */
    @NonNull
    @Override
    public Result doWork() {
        try {
            // Fetch a random quote (using blocking call since we're on background thread)
            Call<Quote> call = RetrofitClient.getQuotesApiService().getRandomQuote();
            Response<Quote> response = call.execute(); // Blocking call is OK here

            if (response.isSuccessful() && response.body() != null) {
                Quote quote = response.body();
                displayNotification(quote);

                // Update last notification time
                PreferencesManager.setLastNotificationTime(System.currentTimeMillis());

                return Result.success();
            } else {
                // API returned error status, retry
                return Result.retry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Network error or other exception, retry
            return Result.retry();
        }
    }

    /**
     * Displays a system notification with the quote.
     *
     * @param quote The quote to display in the notification
     */
    private void displayNotification(Quote quote) {
        Context context = getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create intent to open MainActivity when notification is clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                new android.content.Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                context,
                NOTIFICATION_CHANNEL_ID
        )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Daily Quote")
                .setContentText(quote.getContent())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(quote.getContent() + "\n\n— " + quote.getAuthor()))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
