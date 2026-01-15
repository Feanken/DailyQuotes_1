package com.example.dailyquotes_1.database;

import android.content.Context;

import androidx.room.Room;

/**
 * Singleton class for AppDatabase initialization.
 * Provides lazy initialization using double-checked locking pattern
 * to ensure thread-safe, single instance of the database.
 * 
 * All database operations should go through this provider.
 */
public class DatabaseProvider {

    private static volatile AppDatabase appDatabase;

    /**
     * Private constructor to prevent instantiation.
     */
    private DatabaseProvider() {
    }

    /**
     * Gets or creates the AppDatabase instance using double-checked locking.
     * Ensures thread safety and lazy initialization of the database.
     *
     * @param context Application context for creating the database
     * @return AppDatabase singleton instance
     */
    public static AppDatabase getDatabase(Context context) {
        if (appDatabase == null) {
            synchronized (DatabaseProvider.class) {
                if (appDatabase == null) {
                    appDatabase = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "daily_quotes_database"
                    ).build();
                }
            }
        }
        return appDatabase;
    }

    /**
     * Gets the current database instance.
     * Should be called after getDatabase() has been called at least once.
     *
     * @return AppDatabase instance, or null if not yet initialized
     */
    public static AppDatabase getInstanceOrNull() {
        return appDatabase;
    }

    /**
     * Closes the database connection.
     * Used for cleanup and testing purposes.
     */
    public static void closeDatabase() {
        if (appDatabase != null) {
            appDatabase.close();
            appDatabase = null;
        }
    }
}
