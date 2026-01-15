package com.example.dailyquotes_1.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dailyquotes_1.R;
import com.example.dailyquotes_1.database.AppDatabase;
import com.example.dailyquotes_1.database.DatabaseProvider;
import com.example.dailyquotes_1.model.Note;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * AddNoteActivity allows users to create new notes or edit existing ones.
 * Features:
 * - Form with title and description input fields
 * - Automatic timestamp capture when saving
 * - Support for both create and edit modes (detected via intent extras)
 * - Delete option via menu
 * - Background database operations using Executor
 */
public class AddNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private MaterialButton saveButton;
    private AppDatabase database;
    private Executor executor;

    private String currentNoteId = null;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * Called when the activity is first created.
     * Initializes UI components and loads note data if in edit mode.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state, if available
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize executor for background database operations
        executor = Executors.newSingleThreadExecutor();

        // Initialize database
        database = DatabaseProvider.getDatabase(this);

        // Initialize UI components
        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);
        saveButton = findViewById(R.id.button_save);

        // Set up save button click listener
        saveButton.setOnClickListener(v -> saveNote());

        // Check if this is edit mode
        String noteId = getIntent().getStringExtra("note_id");
        if (noteId != null) {
            currentNoteId = noteId;
            loadNoteForEditing(noteId);
        }
    }

    /**
     * Loads an existing note for editing.
     * Retrieves note data from database and populates the form fields.
     *
     * @param noteId The ID of the note to load
     */
    private void loadNoteForEditing(String noteId) {
        executor.execute(() -> {
            Note note = database.noteDao().getNoteById(noteId);

            if (note != null) {
                runOnUiThread(() -> {
                    titleEditText.setText(note.getTitle());
                    descriptionEditText.setText(note.getDescription());

                    // Update toolbar title to indicate edit mode
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Edit Note");
                    }
                });
            }
        });
    }

    /**
     * Saves a new note or updates an existing one.
     * Validates input, creates/updates the Note object, and persists to database.
     * Shows appropriate feedback messages on completion.
     */
    private void saveNote() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        // Validate input
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        executor.execute(() -> {
            if (currentNoteId == null) {
                // Create new note
                Note newNote = new Note(title, description, currentDate);
                database.noteDao().insertNote(newNote);

                runOnUiThread(() -> {
                    Toast.makeText(AddNoteActivity.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                // Update existing note
                Note existingNote = database.noteDao().getNoteById(currentNoteId);
                if (existingNote != null) {
                    existingNote.setTitle(title);
                    existingNote.setDescription(description);
                    existingNote.setUpdatedAt(currentDate);

                    database.noteDao().updateNote(existingNote);

                    runOnUiThread(() -> {
                        Toast.makeText(AddNoteActivity.this, "Note updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }
        });
    }

    /**
     * Deletes the currently edited note.
     * Called when delete menu item is selected.
     */
    public void deleteNote() {
        if (currentNoteId == null) {
            Toast.makeText(this, "No note to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            database.noteDao().deleteNoteById(currentNoteId);

            runOnUiThread(() -> {
                Toast.makeText(AddNoteActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    /**
     * Handles the toolbar back button press.
     * Returns to the previous activity without saving.
     *
     * @return true if the home button was pressed, false otherwise
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
