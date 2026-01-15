# Java Implementation - Code Examples & Patterns

This document provides code snippets and patterns used in the Java version of the Daily Quotes & Notes app.

---

## 1. POJO Classes with Gson

### Quote.java (API Model)
```java
public class Quote {
    @SerializedName("content")
    private String content;

    @SerializedName("author")
    private String author;

    @SerializedName("_id")
    private String id;

    @SerializedName("tags")
    private String[] tags;

    // Default constructor for Gson
    public Quote() {}

    // Constructor with parameters
    public Quote(String content, String author) {
        this.content = content;
        this.author = author;
    }

    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}
```

### QuoteResponse.java (Pagination Wrapper)
```java
public class QuoteResponse {
    @SerializedName("results")
    private List<Quote> results;

    @SerializedName("count")
    private int count;

    @SerializedName("skip")
    private int skip;

    @SerializedName("limit")
    private int limit;

    @SerializedName("totalCount")
    private int totalCount;

    // Getters
    public List<Quote> getResults() { return results; }
    public int getTotalCount() { return totalCount; }
}
```

---

## 2. Room Database with Annotations

### Note.java (Entity)
```java
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

    // Constructor with all fields
    public Note(String id, String title, String description, String createdAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor for creating new note (auto-generates UUID)
    @Ignore
    public Note(String title, String description, String createdAt) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    // Default constructor
    public Note() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
```

### NoteDao.java (Data Access)
```java
@Dao
public interface NoteDao {
    @Insert
    long insertNote(Note note);

    @Update
    int updateNote(Note note);

    @Delete
    int deleteNote(Note note);

    @Query("DELETE FROM notes WHERE id = :noteId")
    int deleteNoteById(String noteId);

    @Query("SELECT * FROM notes ORDER BY created_at DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM notes WHERE id = :noteId")
    Note getNoteById(String noteId);
}
```

### AppDatabase.java (Database Class)
```java
@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
}
```

### DatabaseProvider.java (Singleton)
```java
public class DatabaseProvider {
    private static volatile AppDatabase appDatabase;

    private DatabaseProvider() {}

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
}
```

---

## 3. Retrofit with Callbacks

### QuotesApiService.java (Service Interface)
```java
public interface QuotesApiService {
    @GET("quotes")
    Call<QuoteResponse> getQuotes(
            @Query("limit") int limit,
            @Query("skip") int skip
    );

    @GET("random")
    Call<Quote> getRandomQuote();
}
```

### RetrofitClient.java (Singleton with Double-Checked Locking)
```java
public class RetrofitClient {
    private static final String BASE_URL = "https://api.quotable.io/";
    private static volatile Retrofit retrofit;
    private static volatile QuotesApiService quotesApiService;

    private RetrofitClient() {}

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static QuotesApiService getQuotesApiService() {
        if (quotesApiService == null) {
            synchronized (RetrofitClient.class) {
                if (quotesApiService == null) {
                    quotesApiService = getRetrofitInstance()
                            .create(QuotesApiService.class);
                }
            }
        }
        return quotesApiService;
    }
}
```

### Usage in Activity (Callback Pattern)
```java
// QuotesActivity.java
private void loadQuotes() {
    isLoading = true;
    loadingProgressBar.setVisibility(View.VISIBLE);

    int skip = currentPage * PAGE_SIZE;

    RetrofitClient.getQuotesApiService()
            .getQuotes(PAGE_SIZE, skip)
            .enqueue(new Callback<QuoteResponse>() {
                @Override
                public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                    loadingProgressBar.setVisibility(View.GONE);
                    isLoading = false;

                    if (response.isSuccessful() && response.body() != null) {
                        QuoteResponse quoteResponse = response.body();

                        if (currentPage == 0) {
                            quotesAdapter.setQuotes(quoteResponse.getResults());
                        } else {
                            quotesAdapter.addQuotes(quoteResponse.getResults());
                        }

                        // Check if more quotes available
                        int totalLoaded = (currentPage + 1) * PAGE_SIZE;
                        if (totalLoaded >= quoteResponse.getTotalCount()) {
                            hasMoreQuotes = false;
                        }

                        Toast.makeText(QuotesActivity.this,
                                "Loaded " + quoteResponse.getResults().size() + " quotes",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QuotesActivity.this,
                                "Failed: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<QuoteResponse> call, Throwable t) {
                    loadingProgressBar.setVisibility(View.GONE);
                    isLoading = false;
                    Toast.makeText(QuotesActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
}
```

---

## 4. Java Executors for Background Operations

### Using ExecutorService (Database Operations)
```java
// NotesActivity.java
private Executor executor = Executors.newSingleThreadExecutor();

private void loadNotes() {
    executor.execute(() -> {
        // This runs on background thread
        List<Note> notes = database.noteDao().getAllNotes();

        // Switch back to main thread for UI updates
        runOnUiThread(() -> {
            if (notes != null && !notes.isEmpty()) {
                notesAdapter.setNotes(notes);
            } else {
                notesAdapter.setNotes(List.of());
            }
        });
    });
}

private void saveNoteToDatabase(Note note) {
    executor.execute(() -> {
        database.noteDao().insertNote(note);
        
        runOnUiThread(() -> {
            Toast.makeText(AddNoteActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
            finish(); // Return to previous activity
        });
    });
}
```

---

## 5. Timestamp Capture with SimpleDateFormat

### AddNoteActivity.java
```java
private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

    // Capture current timestamp
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    String currentDate = dateFormat.format(new Date());

    // Save to database
    executor.execute(() -> {
        Note newNote = new Note(title, description, currentDate);
        database.noteDao().insertNote(newNote);

        runOnUiThread(() -> {
            Toast.makeText(AddNoteActivity.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    });
}
```

---

## 6. SharedPreferences Management

### PreferencesManager.java (Utility Wrapper)
```java
public class PreferencesManager {
    private static final String PREFS_NAME = "DailyQuotesPreferences";
    private static final String NOTIFICATION_ENABLED_KEY = "notification_enabled";
    private static final String LAST_NOTIFICATION_TIME_KEY = "last_notification_time";

    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(
                    PREFS_NAME,
                    Context.MODE_PRIVATE
            );
        }
    }

    public static void setNotificationEnabled(boolean enabled) {
        sharedPreferences.edit()
                .putBoolean(NOTIFICATION_ENABLED_KEY, enabled)
                .apply();
    }

    public static boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(NOTIFICATION_ENABLED_KEY, false);
    }

    public static void setLastNotificationTime(long timestamp) {
        sharedPreferences.edit()
                .putLong(LAST_NOTIFICATION_TIME_KEY, timestamp)
                .apply();
    }

    public static long getLastNotificationTime() {
        return sharedPreferences.getLong(LAST_NOTIFICATION_TIME_KEY, 0);
    }
}
```

### Usage in Activity
```java
// SettingsActivity.java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    // Initialize PreferencesManager
    PreferencesManager.init(this);

    // Load saved preference
    boolean notificationsEnabled = PreferencesManager.isNotificationEnabled();
    notificationSwitch.setChecked(notificationsEnabled);

    // Handle toggle
    notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
        PreferencesManager.setNotificationEnabled(isChecked);
        
        if (isChecked) {
            scheduleDailyQuoteWorker();
        } else {
            cancelDailyQuoteWorker();
        }
    });
}
```

---

## 7. WorkManager for Periodic Tasks

### DailyQuoteWorker.java (Worker Implementation)
```java
public class DailyQuoteWorker extends Worker {
    private static final String NOTIFICATION_CHANNEL_ID = "daily_quotes_channel";
    private static final int NOTIFICATION_ID = 42;

    public DailyQuoteWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Blocking call is OK here (on background thread)
            Call<Quote> call = RetrofitClient.getQuotesApiService().getRandomQuote();
            Response<Quote> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                displayNotification(response.body());
                PreferencesManager.setLastNotificationTime(System.currentTimeMillis());
                return Result.success();
            } else {
                return Result.retry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private void displayNotification(Quote quote) {
        Context context = getApplicationContext();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Daily Quotes",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create notification
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

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

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
```

### Scheduling in SettingsActivity
```java
private static final String DAILY_QUOTE_WORK_TAG = "daily_quote_work";

private void scheduleDailyQuoteWorker() {
    PeriodicWorkRequest dailyQuoteWork =
            new PeriodicWorkRequest.Builder(
                    DailyQuoteWorker.class,
                    15,
                    TimeUnit.MINUTES
            )
            .addTag(DAILY_QUOTE_WORK_TAG)
            .build();

    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DAILY_QUOTE_WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyQuoteWork
    );
}

private void cancelDailyQuoteWorker() {
    WorkManager.getInstance(this).cancelAllWorkByTag(DAILY_QUOTE_WORK_TAG);
}
```

---

## 8. RecyclerView with ViewHolder Pattern

### QuotesAdapter.java
```java
public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder> {
    private List<Quote> quotes = new ArrayList<>();

    @NonNull
    @Override
    public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new QuoteViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteViewHolder holder, int position) {
        Quote quote = quotes.get(position);
        holder.bind(quote);
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public void setQuotes(List<Quote> quoteList) {
        this.quotes = new ArrayList<>(quoteList);
        notifyDataSetChanged();
    }

    public void addQuotes(List<Quote> newQuotes) {
        int startPosition = quotes.size();
        this.quotes.addAll(newQuotes);
        notifyItemRangeInserted(startPosition, newQuotes.size());
    }

    public static class QuoteViewHolder extends RecyclerView.ViewHolder {
        private TextView quoteTextView;
        private TextView authorTextView;

        public QuoteViewHolder(@NonNull LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_quote, parent, false));
            quoteTextView = itemView.findViewById(R.id.text_quote_content);
            authorTextView = itemView.findViewById(R.id.text_quote_author);
        }

        public void bind(Quote quote) {
            quoteTextView.setText(quote.getContent());
            authorTextView.setText("— " + quote.getAuthor());
        }
    }
}
```

### NotesAdapter.java with Click Listener
```java
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private final OnNoteClickListener onNoteClickListener;

    public interface OnNoteClickListener {
        void onNoteClicked(Note note);
    }

    public NotesAdapter(OnNoteClickListener onNoteClickListener) {
        this.onNoteClickListener = onNoteClickListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NoteViewHolder(inflater, parent, onNoteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> noteList) {
        this.notes = new ArrayList<>(noteList);
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView noteTitleTextView;
        private TextView noteDescriptionTextView;
        private TextView noteDateTextView;
        private Note currentNote;

        public NoteViewHolder(@NonNull LayoutInflater inflater, ViewGroup parent,
                             OnNoteClickListener onNoteClickListener) {
            super(inflater.inflate(R.layout.item_note, parent, false));

            noteTitleTextView = itemView.findViewById(R.id.text_note_title);
            noteDescriptionTextView = itemView.findViewById(R.id.text_note_description);
            noteDateTextView = itemView.findViewById(R.id.text_note_date);

            itemView.setOnClickListener(v -> {
                if (onNoteClickListener != null && currentNote != null) {
                    onNoteClickListener.onNoteClicked(currentNote);
                }
            });
        }

        public void bind(Note note) {
            this.currentNote = note;
            noteTitleTextView.setText(note.getTitle());
            noteDescriptionTextView.setText(note.getDescription());
            noteDescriptionTextView.setMaxLines(3);
            noteDateTextView.setText(note.getCreatedAt());
        }
    }
}
```

---

## 9. Intent Navigation Between Activities

### MainActivity.java
```java
private void setupNavigationButtons() {
    Button quotesButton = findViewById(R.id.btn_quotes);
    quotesButton.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, QuotesActivity.class);
        startActivity(intent);
    });

    Button notesButton = findViewById(R.id.btn_notes);
    notesButton.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, NotesActivity.class);
        startActivity(intent);
    });

    Button settingsButton = findViewById(R.id.btn_settings);
    settingsButton.setOnClickListener(v -> {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    });
}
```

### NotesActivity.java (Passing Data via Intent)
```java
private void onNoteClicked(Note note) {
    Intent intent = new Intent(this, AddNoteActivity.class);
    intent.putExtra("note_id", note.getId());
    startActivity(intent);
}
```

### AddNoteActivity.java (Receiving Intent Data)
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_note);

    // Check if editing existing note
    Intent intent = getIntent();
    if (intent != null && intent.hasExtra("note_id")) {
        String currentNoteId = intent.getStringExtra("note_id");
        loadNoteForEditing(currentNoteId);
    }
}

private void loadNoteForEditing(String noteId) {
    executor.execute(() -> {
        Note note = database.noteDao().getNoteById(noteId);
        
        if (note != null) {
            runOnUiThread(() -> {
                titleEditText.setText(note.getTitle());
                descriptionEditText.setText(note.getDescription());
            });
        }
    });
}
```

---

## 10. Pagination with RecyclerView Scroll Listener

```java
// QuotesActivity.java
private void setupScrollListener(LinearLayoutManager layoutManager) {
    quotesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            int totalItemCount = quotesAdapter.getItemCount();

            // Load more when user is near the end (within 3 items)
            if (!isLoading && hasMoreQuotes &&
                    lastVisibleItemPosition >= totalItemCount - 3) {
                currentPage++;
                loadQuotes();
            }
        }
    });
}
```

---

## Summary

These patterns demonstrate:
- ✅ **POJO Serialization**: Using Gson annotations for API responses
- ✅ **Room ORM**: Entity mapping, DAO operations, singleton database provider
- ✅ **Retrofit Callbacks**: Async HTTP with `.enqueue()`
- ✅ **Background Threading**: Java Executors for database and WorkManager for periodic tasks
- ✅ **Timestamp Handling**: SimpleDateFormat for date capture
- ✅ **Preferences Management**: SharedPreferences wrapper
- ✅ **RecyclerView**: Adapter pattern with ViewHolder
- ✅ **Activity Navigation**: Intent-based with data passing

All patterns follow Java best practices and are suitable for production code and academic submission.

