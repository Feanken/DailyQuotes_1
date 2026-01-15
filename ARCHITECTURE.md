# Architecture & Design Patterns

## 📐 Application Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     USER INTERFACE LAYER (UI)                    │
│                        (Android Activities)                       │
├────────────────────┬───────────────────┬─────────────┬──────────┤
│   MainActivity     │  QuotesActivity   │ NotesActivity       │
│                    │                   │ AddNoteActivity     │
│                    │                   │ SettingsActivity    │
└────────────────────┴───────────────────┴─────────────┴──────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                   PRESENTATION LAYER (Adapters)                  │
├────────────────────────────────────────────────────────────────┤
│   QuotesAdapter                    NotesAdapter                 │
│   - ViewHolder implementation      - Click listeners             │
│   - Pagination support             - UI binding                  │
└────────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────────┐
│                      DATA LAYER (Sources)                        │
├─────────────────────────┬──────────────────────────────────────┤
│   Remote (Network)      │   Local (Database)      │ Preferences │
├─────────────────────────┼──────────────────────────┼─────────────┤
│ RetrofitClient          │ AppDatabase             │ Preferences │
│ QuotesApiService        │ NoteDao                 │ Manager     │
│                         │ DatabaseProvider        │             │
└─────────────────────────┴──────────────────────────┴─────────────┘
        ↓                          ↓                        ↓
┌─────────────────┐      ┌─────────────────┐      ┌──────────────┐
│ Quotable API    │      │  Room Database  │      │SharedPrefs   │
│ (External)      │      │  (SQLite)       │      │ (System)     │
└─────────────────┘      └─────────────────┘      └──────────────┘
```

---

## 🔀 Class Dependency Diagram

```
MainActivity
    ├── QuotesActivity (Intent)
    ├── NotesActivity (Intent)
    └── SettingsActivity (Intent)

QuotesActivity
    ├── QuotesAdapter
    ├── RetrofitClient
    └── QuotesApiService

NotesActivity
    ├── NotesAdapter
    ├── DatabaseProvider
    └── AddNoteActivity (Intent)

AddNoteActivity
    ├── DatabaseProvider
    ├── NoteDao
    └── Note (Entity)

SettingsActivity
    ├── PreferencesManager
    ├── WorkManager
    └── DailyQuoteWorker

DailyQuoteWorker
    ├── RetrofitClient
    ├── QuotesApiService
    └── NotificationManager
```

---

## 🏗️ Package Structure

```
com.example.dailyquotes_1
│
├── ui/                    ← Activities (UI Layer)
│   ├── MainActivity
│   ├── QuotesActivity
│   ├── NotesActivity
│   ├── AddNoteActivity
│   └── SettingsActivity
│
├── data/                  ← Data Layer
│   ├── model/             ← Data classes
│   │   ├── Quote
│   │   └── Note
│   ├── remote/            ← API communication
│   │   ├── QuotesApiService
│   │   └── RetrofitClient
│   └── local/             ← Database access
│       ├── NoteDao
│       ├── AppDatabase
│       └── DatabaseProvider
│
├── adapter/               ← RecyclerView adapters
│   ├── QuotesAdapter
│   └── NotesAdapter
│
├── workers/               ← Background services
│   └── DailyQuoteWorker
│
└── utils/                 ← Utilities
    └── PreferencesManager
```

---

## 🔄 Data Flow Sequence Diagrams

### Scenario 1: Loading Quotes

```
QuotesActivity                RetrofitClient         API
      │                             │                  │
      ├──── loadQuotes() ───────────>                  │
      │                             │                  │
      │                             ├── GET /quotes ──>│
      │                             │                  │
      │                             │<─ JSON Response ─┤
      │                             │                  │
      │<─ List<Quote> returned ─────┤                  │
      │                             │                  │
      ├─ setQuotes() ──────────────>│
      │   (to adapter)              │
      │                             │
      ├─ notifyDataSetChanged()     │
      │                             │
      └─ Display quotes             │
         in RecyclerView            │
```

### Scenario 2: Creating a Note

```
AddNoteActivity        DatabaseProvider      NoteDao        Database
      │                      │                  │               │
      ├─ User enters data    │                  │               │
      │                      │                  │               │
      ├─ Click Save ─────────>                  │               │
      │                      │                  │               │
      ├─ Create timestamp    │                  │               │
      │                      │                  │               │
      ├─ Build Note object ──>                  │               │
      │                      │                  │               │
      │                      ├─ getNoteDao() ──>│               │
      │                      │<─ NoteDao ───────┤               │
      │                      │                  │               │
      │                      │  insertNote() ──>│               │
      │                      │                  ├─ INSERT SQL ─>│
      │                      │                  │<─ Success ────┤
      │                      │                  │               │
      ├─ finish() ───────────┤                  │               │
      │                      │                  │               │
      └─ Return to list      │                  │               │
```

### Scenario 3: Daily Notification

```
WorkManager         DailyQuoteWorker     RetrofitClient      API
      │                    │                    │               │
      ├─ Schedule task ───>│                    │               │
      │                    │                    │               │
      │ (every 15 mins)    │                    │               │
      │                    ├─ doWork() ────────>│               │
      │                    │                    │               │
      │                    │                    ├─ GET /random ─>│
      │                    │                    │                │
      │                    │                    │<─ Quote JSON ──┤
      │                    │<─ Quote returned ──┤                │
      │                    │                    │                │
      │                    ├─ Create Notification               │
      │                    │   - Title: "Daily Quote"           │
      │                    │   - Text: Quote content            │
      │                    │   - Author info                     │
      │                    │                                     │
      │                    ├─ NotificationManager.notify()       │
      │                    │                                     │
      │                    ├─ Push to device                     │
      │                    │                                     │
      │<─ Return Success ──┤                                     │
```

---

## 🔧 Design Patterns Used

### 1. Singleton Pattern

```kotlin
// RetrofitClient
object RetrofitClient {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
}

// DatabaseProvider
object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null
    
    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(...).build()
            INSTANCE = instance
            instance
        }
    }
}

// PreferencesManager
class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("daily_quotes_prefs", Context.MODE_PRIVATE)
}
```

**Benefits**:
- Single instance across app
- Thread-safe access
- Efficient resource usage

### 2. ViewHolder Pattern

```kotlin
class QuotesAdapter : RecyclerView.Adapter<QuotesAdapter.QuoteViewHolder>() {
    
    inner class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textQuote: TextView = itemView.findViewById(R.id.text_quote)
        private val textAuthor: TextView = itemView.findViewById(R.id.text_author)
        
        fun bind(quote: Quote) {
            textQuote.text = quote.text
            textAuthor.text = "— ${quote.author}"
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quote, parent, false)
        return QuoteViewHolder(itemView)
    }
}
```

**Benefits**:
- Reuses item views
- Efficient memory usage
- Smooth scrolling

### 3. Repository Pattern

```kotlin
// Implied through:
// - RetrofitClient for remote data
// - DatabaseProvider + NoteDao for local data
// - PreferencesManager for user preferences

// Activities never directly access:
// - Network calls (use service)
// - Database queries (use DAO)
// - Preferences (use manager)
```

**Benefits**:
- Abstraction of data sources
- Easy to test
- Easy to swap implementations

### 4. Observer Pattern

```kotlin
// Room Flow provides reactive updates
val allNotes: Flow<List<Note>> = noteDao.getAllNotes()

lifecycleScope.launch {
    allNotes.collect { notes ->
        notesAdapter.setNotes(notes)
    }
}
```

**Benefits**:
- Auto-update UI on data change
- No manual refresh needed
- Lifecycle-aware

### 5. Builder Pattern

```kotlin
// Retrofit configuration
val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

// Notification creation
val notification = NotificationCompat.Builder(context, channelId)
    .setSmallIcon(R.drawable.ic_launcher_foreground)
    .setContentTitle("Daily Quote")
    .setStyle(NotificationCompat.BigTextStyle().bigText(...))
    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    .build()

// WorkManager request
val dailyQuoteWork = PeriodicWorkRequestBuilder<DailyQuoteWorker>(
    15,
    TimeUnit.MINUTES
)
    .addTag(DAILY_QUOTE_WORK_TAG)
    .build()
```

**Benefits**:
- Clean, readable configuration
- Flexible object creation
- Immutable result objects

---

## 🔐 Data Storage Mechanisms

### Local Storage Comparison

```
┌──────────────┬───────────────────┬─────────────────┬─────────────┐
│ Mechanism    │ Use Case          │ Capacity        │ Security    │
├──────────────┼───────────────────┼─────────────────┼─────────────┤
│ Room DB      │ Complex data      │ Large (GB)      │ Encrypted   │
│              │ with queries      │                 │ per device  │
├──────────────┼───────────────────┼─────────────────┼─────────────┤
│SharedPrefs   │ Simple key-value  │ Small (< 1MB)   │ Unencrypted │
│              │ user preferences  │                 │ by default  │
├──────────────┼───────────────────┼─────────────────┼─────────────┤
│Files         │ Large binary data │ Large           │ Per device  │
│              │ documents, images │                 │             │
└──────────────┴───────────────────┴─────────────────┴─────────────┘
```

**This App Uses**:
- **Room** for Notes (complex data, CRUD operations)
- **SharedPreferences** for Settings (simple boolean flag)
- **API Cache** (not implemented, could be added)

---

## 📊 Entity Relationship Diagram

```
┌──────────────────────────────────┐
│          NOTES TABLE             │
├──────────────────────────────────┤
│ id (Primary Key)                 │
│ title (String)                   │
│ description (String)             │
│ createdAt (String - Timestamp)   │
│ updatedAt (String - Timestamp)   │
└──────────────────────────────────┘
         ↑
         │ (1:1 relationship)
         │
    Referenced by AddNoteActivity
    for editing/deletion

┌──────────────────────────────────┐
│     SHARED PREFERENCES           │
├──────────────────────────────────┤
│ notification_enabled (Boolean)   │
│ last_notification_time (Long)    │
└──────────────────────────────────┘
         ↑
         │
    Referenced by SettingsActivity
```

---

## 🌐 Network Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   QUOTABLE API (External)                    │
│                 https://api.quotable.io/                     │
├─────────────────────────────────────────────────────────────┤
│ Endpoints:                                                  │
│ - GET /quotes?limit=10&skip=0  → List of quotes            │
│ - GET /random                 → Random quote                │
└─────────────────────────────────────────────────────────────┘
         ↑
         │ (HTTPS, JSON)
         │
┌─────────────────────────────────────────────────────────────┐
│               RETROFIT HTTP CLIENT (App)                     │
├─────────────────────────────────────────────────────────────┤
│ - Handles HTTP requests/responses                           │
│ - Converts JSON to Kotlin objects (Gson)                    │
│ - Manages connection pooling                                │
└─────────────────────────────────────────────────────────────┘
         ↑
         │
┌─────────────────────────────────────────────────────────────┐
│              ACTIVITIES (Request Data)                       │
│        Uses RetrofitClient.quotesApiService                 │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎬 Activity Lifecycle & State Management

```
MainActivity (Launcher)
     │
     ├─ onCreate() ────────> Initialize UI, set click listeners
     ├─ onStart() 
     ├─ onResume() ────────> App visible
     │
     ├─ [User navigates]
     │
     ├─ onPause() 
     ├─ onStop() ──────────> App hidden
     └─ onDestroy() ────────> Activity destroyed

QuotesActivity (opened from MainActivity)
     │
     ├─ onCreate() ────────> Set up RecyclerView, load quotes
     ├─ onStart()
     ├─ onResume() ────────> API calls in lifecycleScope.launch
     │                       Coroutines auto-cancelled on destroy
     │
     ├─ [User scrolls]
     │   ├─ RecyclerView scroll listener triggered
     │   └─ loadQuotes() called with new page
     │
     ├─ [User navigates back]
     │
     ├─ onPause()
     ├─ onStop()
     └─ onDestroy() ────────> RecyclerView cleaned up
                              Coroutines cancelled

NotesActivity (Database-driven)
     │
     ├─ onCreate() ────────> Initialize DAO, set up Flow
     ├─ onResume() ────────> Collect from notes Flow
     │                       Updates UI on database change
     │
     ├─ [User adds note]
     │   ├─ Opens AddNoteActivity
     │
     ├─ onPause()
     └─ onResume() ────────> Refresh notes list

AddNoteActivity (Database write)
     │
     ├─ onCreate()
     │   ├─ Check for "note_id" intent extra
     │   ├─ If exists: Load note for editing
     │   └─ If null: Create new note
     │
     ├─ [User saves]
     │   ├─ Get current timestamp
     │   ├─ Create Note object
     │   ├─ Insert/Update in database
     │   ├─ finish() ────────> Return to NotesActivity
     │
     └─ onDestroy()

SettingsActivity (Preferences)
     │
     ├─ onCreate() ────────> Load notification state
     │                       Set switch position
     │
     ├─ onResume() ────────> Ready to toggle
     │
     ├─ [User toggles]
     │   ├─ Save to SharedPreferences
     │   └─ Schedule/cancel WorkManager
     │
     └─ onDestroy() ────────> State persisted in preferences
```

---

## ⚙️ WorkManager Background Task Diagram

```
┌─────────────────────────────────────────────────────┐
│         SettingsActivity.scheduleNotifications()    │
└────────────────────┬────────────────────────────────┘
                     │
                     ├─ Create PeriodicWorkRequest
                     │   │
                     │   ├─ Worker: DailyQuoteWorker
                     │   ├─ Interval: 15 minutes
                     │   ├─ Tag: "daily_quote_work"
                     │   └─ Policy: KEEP (don't replace)
                     │
                     └─ WorkManager.enqueueUniquePeriodicWork()
                        │
                        └─ Device Scheduler
                           │
                           ├─ Wait 15 minutes
                           │
                           ├─ Trigger DailyQuoteWorker
                           │   │
                           │   ├─ doWork() {
                           │   │   ├─ Fetch random quote
                           │   │   ├─ Create notification
                           │   │   └─ Return Result.success()
                           │   │ }
                           │
                           └─ Reschedule for next 15 minutes
                              (repeats indefinitely)

┌─────────────────────────────────────────────────────┐
│      SettingsActivity.cancelNotifications()         │
└────────────────────┬────────────────────────────────┘
                     │
                     └─ WorkManager.cancelAllWorkByTag()
                        │
                        └─ Remove "daily_quote_work" tag
                           │
                           └─ Stop periodic execution
```

---

## 🔍 Component Interaction Map

```
┌────────────────────────────────────────────────────────────────┐
│                    MAIN ACTIVITY                                │
│                    ┌──────────────────────┐                    │
│                    │ Navigation Hub       │                    │
│                    │ - Quotes button ────────→ QuotesActivity  │
│                    │ - Notes button ─────────→ NotesActivity   │
│                    │ - Settings button ──────→ Settings...     │
│                    └──────────────────────┘                    │
└────────────────────────────────────────────────────────────────┘
         ↑
         │ Every Activity has
         │ Option Menu linking
         │ back to navigation
         │
      ┌──┴──────────────┬──────────────┬─────────────┐
      │                 │              │             │
      ↓                 ↓              ↓             ↓
   Quotes         Notes          Add Note      Settings
   Activity       Activity        Activity      Activity
      │              │              │             │
      ├─ API ────────┤              │             │
      │              │              │             │
      │              ├─ Database ──┘             │
      │              │                          │
      │              └──────────────────────────┘
      │                                         │
      └─ SharedPreferences (Notifications) ────┘
         │
         └─ WorkManager (Background task)
```

---

**Diagram Last Updated**: December 27, 2025
**Architecture Type**: MVVM-inspired (Model-View pattern)
**Scalability**: Ready for expansion (easy to add features)
