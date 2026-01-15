package com.example.dailyquotes_1.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dailyquotes_1.model.Note;

import java.util.List;

/**
 * Room Data Access Object (DAO) interface for Note entity.
 * Provides abstract methods for CRUD operations on the notes table.
 * Methods will be implemented by Room at compile time.
 */
@Dao
public interface NoteDao {

    /**
     * Inserts a single note into the database.
     *
     * @param note The note to insert
     * @return The row ID of the inserted note
     */
    @Insert
    long insertNote(Note note);

    /**
     * Updates an existing note in the database.
     *
     * @param note The note to update
     * @return Number of rows updated
     */
    @Update
    int updateNote(Note note);

    /**
     * Deletes a specific note from the database.
     *
     * @param note The note to delete
     * @return Number of rows deleted
     */
    @Delete
    int deleteNote(Note note);

    /**
     * Deletes a note by its ID.
     *
     * @param noteId The ID of the note to delete
     * @return Number of rows deleted
     */
    @Query("DELETE FROM notes WHERE id = :noteId")
    int deleteNoteById(String noteId);

    /**
     * Retrieves all notes from the database.
     * Returns a regular List (not Flow) for Java implementation.
     *
     * @return List of all notes sorted by creation date (newest first)
     */
    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    List<Note> getAllNotes();

    /**
     * Retrieves a specific note by its ID.
     *
     * @param noteId The ID of the note to retrieve
     * @return The requested note, or null if not found
     */
    @Query("SELECT * FROM notes WHERE id = :noteId")
    Note getNoteById(String noteId);

    /**
     * Deletes all notes from the database.
     * Used primarily for testing and clearing all data.
     *
     * @return Number of rows deleted
     */
    @Query("DELETE FROM notes")
    int deleteAllNotes();

    /**
     * Gets the count of all notes in the database.
     *
     * @return Number of notes
     */
    @Query("SELECT COUNT(*) FROM notes")
    int getNoteCount();
}
