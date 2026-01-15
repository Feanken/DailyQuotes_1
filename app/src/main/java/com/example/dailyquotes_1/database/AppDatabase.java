package com.example.dailyquotes_1.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.dailyquotes_1.model.Note;

/**
 * Room Database abstract class defining the database schema.
 * This class serves as the main access point for the underlying connection
 * to the app's persisted, relational data.
 * 
 * Version History:
 * - Version 1: Initial schema with notes table
 */
@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Provides access to the NoteDao for database operations.
     *
     * @return NoteDao instance for interacting with the notes table
     */
    public abstract NoteDao noteDao();
}
