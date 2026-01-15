package com.example.dailyquotes_1.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for managing SharedPreferences.
 * Provides methods to store and retrieve user settings,
 * particularly notification preferences.
 */
public class PreferencesManager {

    private static final String PREFS_NAME = "DailyQuotesPreferences";
    private static final String NOTIFICATION_ENABLED_KEY = "notification_enabled";
    private static final String LAST_NOTIFICATION_TIME_KEY = "last_notification_time";

    private static SharedPreferences sharedPreferences;

    /**
     * Private constructor to prevent instantiation.
     */
    private PreferencesManager() {
    }

    /**
     * Initializes the SharedPreferences instance.
     * Should be called once in the application's onCreate method.
     *
     * @param context Application context
     */
    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    /**
     * Ensures SharedPreferences is initialized.
     * Throws IllegalStateException if initialization has not occurred.
     */
    private static void ensureInitialized() {
        if (sharedPreferences == null) {
            throw new IllegalStateException("PreferencesManager must be initialized with init() before use");
        }
    }

    /**
     * Sets whether daily quote notifications are enabled.
     *
     * @param enabled true to enable notifications, false to disable
     */
    public static void setNotificationEnabled(boolean enabled) {
        ensureInitialized();
        sharedPreferences.edit()
                .putBoolean(NOTIFICATION_ENABLED_KEY, enabled)
                .apply();
    }

    /**
     * Checks if daily quote notifications are enabled.
     *
     * @return true if notifications are enabled, false otherwise
     */
    public static boolean isNotificationEnabled() {
        ensureInitialized();
        return sharedPreferences.getBoolean(NOTIFICATION_ENABLED_KEY, false);
    }

    /**
     * Sets the timestamp of the last notification sent.
     *
     * @param timestamp The timestamp in milliseconds
     */
    public static void setLastNotificationTime(long timestamp) {
        ensureInitialized();
        sharedPreferences.edit()
                .putLong(LAST_NOTIFICATION_TIME_KEY, timestamp)
                .apply();
    }

    /**
     * Gets the timestamp of the last notification sent.
     *
     * @return The last notification timestamp, or 0 if never set
     */
    public static long getLastNotificationTime() {
        ensureInitialized();
        return sharedPreferences.getLong(LAST_NOTIFICATION_TIME_KEY, 0);
    }

    /**
     * Clears all stored preferences.
     * Used for cleanup and testing purposes.
     */
    public static void clearAll() {
        ensureInitialized();
        sharedPreferences.edit().clear().apply();
    }
}
