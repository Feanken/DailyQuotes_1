package com.example.dailyquotes_1.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailyquotes_1.R;
import com.example.dailyquotes_1.model.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for displaying notes.
 * Uses the ViewHolder pattern for efficient view reuse.
 * Supports click listeners for editing notes.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private final OnNoteClickListener onNoteClickListener;

    /**
     * Interface for handling note item click events.
     */
    public interface OnNoteClickListener {
        /**
         * Called when a note item is clicked.
         *
         * @param note The note that was clicked
         */
        void onNoteClicked(Note note);
    }

    /**
     * Constructor initializes the notes list and click listener.
     *
     * @param onNoteClickListener Callback for handling note clicks
     */
    public NotesAdapter(OnNoteClickListener onNoteClickListener) {
        this.notes = new ArrayList<>();
        this.onNoteClickListener = onNoteClickListener;
    }

    /**
     * Creates a new ViewHolder when RecyclerView needs one.
     *
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new NoteViewHolder that holds the View for displaying a note
     */
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NoteViewHolder(inflater, parent, onNoteClickListener);
    }

    /**
     * Binds the data to the ViewHolder for the note at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at position
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.bind(note);
    }

    /**
     * Returns the total number of items in the data set.
     *
     * @return The number of notes in the list
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Sets the notes list and notifies the adapter of the change.
     * Replaces all items in the adapter.
     *
     * @param noteList The new list of notes to display
     */
    public void setNotes(List<Note> noteList) {
        this.notes = new ArrayList<>(noteList);
        notifyDataSetChanged();
    }

    /**
     * Inner ViewHolder class for displaying individual note items.
     */
    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        private final TextView noteTitleTextView;
        private final TextView noteDescriptionTextView;
        private final TextView noteDateTextView;
        private final OnNoteClickListener onNoteClickListener;
        private Note currentNote;

        /**
         * Constructor initializes the views for displaying note information.
         *
         * @param inflater LayoutInflater for inflating the item layout
         * @param parent The parent ViewGroup
         * @param onNoteClickListener Callback for handling note clicks
         */
        public NoteViewHolder(@NonNull LayoutInflater inflater, ViewGroup parent,
                              OnNoteClickListener onNoteClickListener) {
            super(inflater.inflate(R.layout.item_note, parent, false));

            noteTitleTextView = itemView.findViewById(R.id.text_note_title);
            noteDescriptionTextView = itemView.findViewById(R.id.text_note_description);
            noteDateTextView = itemView.findViewById(R.id.text_note_date);
            this.onNoteClickListener = onNoteClickListener;

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                if (onNoteClickListener != null && currentNote != null) {
                    onNoteClickListener.onNoteClicked(currentNote);
                }
            });
        }

        /**
         * Binds a note to this ViewHolder's views.
         *
         * @param note The note data to display
         */
        public void bind(Note note) {
            this.currentNote = note;

            noteTitleTextView.setText(note.getTitle());

            // Truncate description to 3 lines
            noteDescriptionTextView.setText(note.getDescription());
            noteDescriptionTextView.setMaxLines(3);

            noteDateTextView.setText(note.getCreatedAt());
        }
    }
}
