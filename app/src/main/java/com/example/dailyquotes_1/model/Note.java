package com.example.dailyquotes_1.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

/**
 * Room Entity class representing a Note in the database.
 * This class is automatically mapped to the 'notes' table in SQLite.
 */
@Entity(tableName = "notes")
public class Note {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "updated_at")
    private String updatedAt;

    /**
     * Constructor with all fields.
     *
     * @param id Unique identifier (UUID)
     * @param title Note title
     * @param description Note description
     * @param createdAt Timestamp when note was created
     * @param updatedAt Timestamp when note was last updated
     */
    public Note(String id, String title, String description, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Constructor with title, description, and creation timestamp.
     * Automatically generates UUID for id.
     *
     * @param title Note title
     * @param description Note description
     * @param createdAt Timestamp when note was created
     */
    @Ignore
    public Note(String title, String description, String createdAt) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    /**
     * Default constructor required by Room.
     */
    public Note() {
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
