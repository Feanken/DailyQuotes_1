# Implementation Notes & Quick Reference

## 📌 Important Implementation Details

### Activities & Their Responsibilities

#### 1. MainActivity (Launcher)
```kotlin
// Entry point of application
// Exports = true (required for launcher)
// No parent activity
```

#### 2. QuotesActivity
```kotlin
// Implements pagination
// API calls in lifecycleScope.launch
// RecyclerView with scroll listener
// Progress bar management
```
**Key Methods**:
- `loadQuotes()` - Fetches quotes from API
- Scroll listener triggers pagination

#### 3. NotesActivity
```kotlin
// Database access through DatabaseProvider
// Flow collection for reactive updates
// Refreshes on resume
```
**Key Methods**:
- Load all notes from database
- Navigate to AddNoteActivity with note ID

#### 4. AddNoteActivity
```kotlin
// Handles both create and edit
// Detects mode by checking "note_id" intent extra
// Auto-captures timestamp on save
```
**Key Logic**:
- If `note_id` exists → Edit mode
- Save creates UUID for new notes
- Delete requires edit mode

#### 5. SettingsActivity
```kotlin
// Single toggle switch
// Persists state to SharedPreferences
// Immediate WorkManager scheduling
```
**Key Methods**:
- `scheduleNotifications()` - Enqueues periodic work
- `cancelNotifications()` - Cancels all work by tag

---

## 🔑 Critical Implementation Points

### 1. **Room Database Initialization**
```kotlin
// In activities, always use DatabaseProvider
val database = DatabaseProvider.getDatabase(context)
val dao = database.noteDao()
```
❌ DON'T: Create database instances directly
✅ DO: Use DatabaseProvider singleton

### 2. **Coroutine Scope**
```kotlin
// Use lifecycleScope in activities
lifecycleScope.launch {
    // Safe cancellation on activity destroy
}
```
❌ DON'T: Use GlobalScope
✅ DO: Use lifecycleScope or viewModelScope

### 3. **API Error Handling**
```kotlin
try {
    val data = apiService.getQuotes(...)
    // Update UI
} catch (e: Exception) {
    // Show error to user
    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
}
```
❌ DON'T: Let exceptions crash app
✅ DO: Catch and inform user

### 4. **RecyclerView Pagination**
```kotlin
// Check if scrolled to end
if (visibleItemCount + firstVisiblePosition >= totalItemCount - 3) {
    loadMore()
}
```
❌ DON'T: Load on every scroll event
✅ DO: Load 3 items before end

### 5. **Notification Permission**
```xml
<!-- Add to AndroidManifest.xml -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```
❌ DON'T: Forget this permission
✅ DO: Declare in manifest

### 6. **Intent Extras**
```kotlin
// Putting extras
val intent = Intent(context, TargetActivity::class.java)
intent.putExtra("note_id", noteId)
startActivity(intent)

// Getting extras
val noteId = intent.getStringExtra("note_id")
```
❌ DON'T: Assume extras exist without checking
✅ DO: Check if null before use

---

## 🎯 Feature Implementation Checklist

### Quotes Feature
- [x] API service interface
- [x] Data model with Gson annotations
- [x] Retrofit client singleton
- [x] QuotesActivity with RecyclerView
- [x] QuotesAdapter with pagination
- [x] Scroll listener for load more
- [x] Progress bar UI
- [x] Error handling

### Notes Feature
- [x] Note entity for Room
- [x] NoteDao with CRUD operations
- [x] AppDatabase configuration
- [x] DatabaseProvider singleton
- [x] NotesActivity with RecyclerView
- [x] NotesAdapter with click listener
- [x] AddNoteActivity with form
- [x] Timestamp auto-capture
- [x] Edit functionality
- [x] Delete functionality

### Notifications Feature
- [x] PreferencesManager for toggle state
- [x] DailyQuoteWorker for background task
- [x] WorkManager periodic scheduling
- [x] Notification channel creation
- [x] Notification builder with content
- [x] Tap action (opens MainActivity)

### Settings Feature
- [x] SettingsActivity with switch
- [x] SharedPreferences integration
- [x] WorkManager control logic
- [x] Persist state across app restarts

### Navigation Feature
- [x] Option menu in all activities
- [x] Intent navigation between activities
- [x] Parent activity configuration
- [x] Back button support

---

## 🧪 Testing Scenarios

### Scenario 1: Load Quotes
1. Launch app → Tap "View Quotes"
2. Should see list of quotes loading
3. Scroll to bottom → More quotes load
4. Author names should be visible

### Scenario 2: Create Note
1. Tap "My Notes"
2. Tap FAB (+) button
3. Enter title and description
4. Tap "Save"
5. Note should appear in list with current timestamp
6. Date format should be: `yyyy-MM-dd HH:mm:ss`

### Scenario 3: Edit Note
1. From notes list, tap any note
2. Data should pre-populate in form
3. Modify text
4. Tap "Save"
5. Changes should reflect in list

### Scenario 4: Delete Note
1. From add note screen (editing)
2. Tap menu → Delete
3. Confirm deletion
4. Return to list
5. Note should be gone

### Scenario 5: Enable Notifications
1. Tap "Settings"
2. Toggle switch ON
3. Toast should show "Daily notifications enabled"
4. WorkManager should schedule task
5. After 15 minutes, notification should appear

### Scenario 6: Disable Notifications
1. From Settings, toggle switch OFF
2. Toast should show "Daily notifications disabled"
3. WorkManager should cancel task
4. No more notifications should appear

### Scenario 7: App Restart
1. Enable notifications
2. Add a note
3. Close app completely
4. Reopen app
5. Notes should still exist
6. Notification setting should be ON
7. Notifications should still work

---

## 🔐 Security Considerations

### 1. Data Validation
```kotlin
// Always validate user input
if (title.isEmpty() || description.isEmpty()) {
    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
    return
}
```

### 2. Permission Handling
```kotlin
// For Android 12+, might need runtime permission for notifications
// Currently declared in manifest, but runtime request may be needed
```

### 3. Database Security
```kotlin
// Room handles SQL injection prevention
// UUID primary keys prevent ID guessing
```

### 4. Network Security
```kotlin
// HTTPS used for Quotable API
// Retrofit handles certificate validation
```

---

## 📊 Performance Considerations

### 1. Pagination
- Load 10 quotes per page
- Total API: ~100 quotes available
- Reduces memory usage

### 2. Database Queries
- Flow returns only queried data
- No unnecessary data loading
- Room handles threading

### 3. RecyclerView
- ViewHolder pattern reuses views
- No excessive object creation
- Smooth scrolling experience

### 4. Background Services
- WorkManager handles task scheduling
- Doesn't block main thread
- Automatically handles device constraints

---

## 🔄 Data Flow Diagrams

### Quotes Flow
```
MainActivity
    ↓
QuotesActivity
    ↓
RetrofitClient.quotesApiService
    ↓
https://api.quotable.io/quotes
    ↓
Quote objects (List)
    ↓
QuotesAdapter
    ↓
RecyclerView
    ↓
UI Display
```

### Notes Flow
```
MainActivity
    ↓
NotesActivity
    ↓
DatabaseProvider.getDatabase()
    ↓
NoteDao.getAllNotes()
    ↓
Note objects (Flow)
    ↓
NotesAdapter
    ↓
RecyclerView
    ↓
UI Display

User clicks note
    ↓
AddNoteActivity + note_id
    ↓
NoteDao.getNoteById()
    ↓
Pre-populate form
    ↓
Save/Delete operation
```

### Notifications Flow
```
SettingsActivity
    ↓
Toggle switch ON
    ↓
PreferencesManager.setNotificationEnabled(true)
    ↓
WorkManager.enqueueUniquePeriodicWork()
    ↓
Every 15 minutes:
    DailyQuoteWorker
        ↓
    RetrofitClient.getRandomQuote()
        ↓
    NotificationManager.notify()
        ↓
    Push notification to user
```

---

## 🛠️ Common Modifications

### 1. Change Notification Frequency
```kotlin
// In SettingsActivity.scheduleNotifications()
val dailyQuoteWork = PeriodicWorkRequestBuilder<DailyQuoteWorker>(
    1,              // Change from 15 to 1
    TimeUnit.DAYS   // Change from MINUTES to DAYS
)
```

### 2. Change Quotes Per Page
```kotlin
// In QuotesActivity.loadQuotes()
val response = RetrofitClient.quotesApiService.getQuotes(
    limit = 20,     // Change from 10 to 20
    skip = currentPage * pageSize
)
```

### 3. Customize Notification Content
```kotlin
// In DailyQuoteWorker.showNotification()
.setContentTitle("Custom Title")
.setContentText("Custom text")
.setStyle(NotificationCompat.BigTextStyle()
    .bigText("Custom format: $quoteText\n\n— $author"))
```

### 4. Change Date Format
```kotlin
// In AddNoteActivity
val dateFormat = SimpleDateFormat(
    "dd/MM/yyyy HH:mm",  // Change format string
    Locale.getDefault()
)
```

---

## ⚠️ Known Limitations & Future Improvements

### Current Limitations
1. **Offline Mode**: App requires internet for quotes
2. **Duplicate Prevention**: No check for duplicate notes
3. **Search**: No search functionality
4. **Sync**: Notes not synced to cloud
5. **Authentication**: No user accounts

### Potential Improvements
1. Add offline quote caching
2. Implement local note search
3. Add note categories/tags
4. Cloud backup to Firebase
5. User authentication
6. Note sharing feature
7. Quote favorites
8. Dark theme support
9. Multiple reminder times
10. Note attachments (photos)

---

## 📞 Troubleshooting Reference

| Issue | Solution |
|-------|----------|
| Build fails - Room annotation processor | Add kotlin-kapt plugin |
| API returns 404 | Check API base URL and endpoint |
| Notes disappear on app close | Database not initialized |
| Notifications never arrive | Check WorkManager status, enable notifications in settings |
| Timestamps show wrong time | Check device timezone and system time |
| RecyclerView shows blank | Check adapter data and layout manager |
| Navigation doesn't work | Verify intent class names and manifest declaration |
| Crashes on note edit | Check if note_id extra is null |

---

## 📖 Code Style Guidelines

### Kotlin Naming Conventions
```kotlin
// Classes - PascalCase
class MainActivity

// Functions/Methods - camelCase
fun loadQuotes()

// Properties - camelCase
private val recyclerView: RecyclerView

// Constants - UPPER_SNAKE_CASE
private const val BASE_URL = "https://api.quotable.io/"

// XML Resources - snake_case
activity_main.xml
ic_launcher_foreground.xml
```

### XML Layout Guidelines
```xml
<!-- Always use full package names for custom classes -->
<com.google.android.material.card.MaterialCardView />

<!-- Use meaningful IDs -->
android:id="@+id/btn_save_note"  <!-- Good -->
android:id="@+id/b1"              <!-- Bad -->

<!-- Always set content descriptions -->
android:contentDescription="@string/btn_add_note"
```

---

## 🎓 Learning Resources

### For This Project
- Room Database: Developer guide in Android Studio
- Retrofit: Official documentation at square.github.io/retrofit/
- WorkManager: Android Developers WorkManager docs
- Quotable API: github.com/lukePeavey/quotable

### General Android
- Android Developer Official Docs
- Google Codelabs
- Stack Overflow for specific issues

---

**Last Updated**: December 27, 2025
**Compatibility**: Android 5.0+ (API 24+)
**Status**: Ready for Production
