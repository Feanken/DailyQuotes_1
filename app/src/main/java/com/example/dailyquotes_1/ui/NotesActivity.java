package com.example.dailyquotes_1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyquotes_1.R;
import com.example.dailyquotes_1.database.AppDatabase;
import com.example.dailyquotes_1.database.DatabaseProvider;
import com.example.dailyquotes_1.model.Note;
import com.example.dailyquotes_1.ui.adapter.NotesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * NotesActivity displays a list of user notes with timestamps.
 * Features:
 * - RecyclerView with custom adapter for displaying notes
 * - FAB (Floating Action Button) to create new notes
 * - Click listeners to edit existing notes
 * - Database operations using Room (executed on background thread)
 */
public class NotesActivity extends AppCompatActivity {

    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private AppDatabase database;
    private Executor executor;

    /**
     * Called when the activity is first created.
     * Initializes the database, UI components, and loads notes.
     *
     * @param savedInstanceState Bundle containing activity's previously saved state, if available
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

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
        notesRecyclerView = findViewById(R.id.notes_recycler_view);
        FloatingActionButton fabAddNote = findViewById(R.id.fab_add_note);

        // Set up RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notesRecyclerView.setLayoutManager(layoutManager);

        // Create and set adapter with click listener
        notesAdapter = new NotesAdapter(note -> onNoteClicked(note));
        notesRecyclerView.setAdapter(notesAdapter);

        // Set up FAB click listener for creating new note
        fabAddNote.setOnClickListener(v -> navigateToAddNote(null));

        // Load notes from database
        loadNotes();
    }

    /**
     * Loads all notes from the database on a background thread.
     * Updates the adapter on the main thread when data is retrieved.
     */
    private void loadNotes() {
        executor.execute(() -> {
            List<Note> notes = database.noteDao().getAllNotes();

            // Switch back to main thread to update UI
            runOnUiThread(() -> {
                if (notes != null && !notes.isEmpty()) {
                    notesAdapter.setNotes(notes);
                } else {
                    notesAdapter.setNotes(List.of());
                }
            });
        });
    }

    /**
     * Handles note item click event.
     * Navigates to AddNoteActivity for editing the selected note.
     *
     * @param note The note that was clicked
     */
    private void onNoteClicked(Note note) {
        navigateToAddNote(note);
    }

    /**
     * Navigates to AddNoteActivity for creating a new note or editing an existing one.
     * Passes the note ID to the target activity if editing.
     *
     * @param note The note to edit, or null to create a new note
     */
    private void navigateToAddNote(Note note) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        if (note != null) {
            intent.putExtra("note_id", note.getId());
        }
        startActivity(intent);
    }

    /**
     * Called when the activity is resumed.
     * Reloads notes from the database to reflect any changes made in AddNoteActivity.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
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
