# Daily Quotes & Notes App - Complete Implementation Guide

## 📋 Project Overview
This is a complete Android application built with Kotlin and XML Views that fulfills all academic requirements for a Mobile Programming coursework. The app provides users with daily quotes, note-taking capabilities, and customizable notifications.

## ✅ Core Requirements Met

### 1. **Multiple Activities** ✓
- **MainActivity**: Main dashboard with navigation menu
- **QuotesActivity**: Displays quotes fetched from API
- **NotesActivity**: Displays user's saved notes
- **AddNoteActivity**: Create/Edit notes with timestamps
- **SettingsActivity**: Configure app settings
- All activities use Intent for navigation

### 2. **RecyclerView** ✓
- **QuotesAdapter**: Displays list of quotes with pagination
- **NotesAdapter**: Displays list of user's notes with click listeners
- Both implement efficient list management and smooth scrolling

### 3. **API Integration** ✓
- **Retrofit**: HTTP client for API calls
- **QuotesApiService**: Interface for API endpoints
- **RetrofitClient**: Singleton pattern for API initialization
- Data source: https://api.quotable.io/ (Quotable API)
- Features: Get quotes with pagination, fetch random quotes

### 4. **Menus** ✓
- **Option Menu**: Implemented in all activities for navigation
- **Floating Action Button**: Quick add note button in NotesActivity
- Menu items for Quotes, Notes, and Settings

### 5. **Local Storage** ✓
- **Room Database**: Modern Android persistence library
- **NoteDao**: Data Access Object for CRUD operations
- **AppDatabase**: Database configuration
- DatabaseProvider: Singleton for database access
- Auto-incrementing timestamps on note creation

### 6. **Background Services** ✓
- **WorkManager**: For scheduling daily notifications
- **DailyQuoteWorker**: Coroutine worker that fetches quotes in background
- Runs every 15 minutes (can be adjusted to daily)
- Sends push notifications with fetched quotes

### 7. **Shared Preferences** ✓
- **PreferencesManager**: Utility class for preference management
- Saves notification toggle state
- Persists across app restarts
- Tracks last notification time

## 📁 Project Structure

```
app/src/main/
├── java/com/example/dailyquotes_1/
│   ├── ui/
│   │   ├── MainActivity.kt              # Dashboard with navigation
│   │   ├── QuotesActivity.kt            # Display quotes from API
│   │   ├── NotesActivity.kt             # List user's notes
│   │   ├── AddNoteActivity.kt           # Create/Edit notes
│   │   └── SettingsActivity.kt          # Settings & notification toggle
│   │
│   ├── data/
│   │   ├── model/
│   │   │   ├── Quote.kt                 # Quote data model
│   │   │   └── Note.kt                  # Note data model (Room entity)
│   │   ├── remote/
│   │   │   ├── QuotesApiService.kt      # Retrofit API interface
│   │   │   └── RetrofitClient.kt        # Retrofit client setup
│   │   └── local/
│   │       ├── NoteDao.kt               # Database access object
│   │       ├── AppDatabase.kt           # Room database configuration
│   │       └── DatabaseProvider.kt      # Database singleton
│   │
│   ├── adapter/
│   │   ├── QuotesAdapter.kt             # RecyclerView adapter for quotes
│   │   └── NotesAdapter.kt              # RecyclerView adapter for notes
│   │
│   ├── workers/
│   │   └── DailyQuoteWorker.kt          # WorkManager for daily notifications
│   │
│   └── utils/
│       └── PreferencesManager.kt         # SharedPreferences utility
│
├── res/
│   ├── layout/
│   │   ├── activity_main.xml            # Main dashboard layout
│   │   ├── activity_quotes.xml          # Quotes list layout
│   │   ├── activity_notes.xml           # Notes list layout
│   │   ├── activity_add_note.xml        # Add/Edit note layout
│   │   ├── activity_settings.xml        # Settings layout
│   │   ├── item_quote.xml               # Quote card item layout
│   │   └── item_note.xml                # Note card item layout
│   │
│   ├── menu/
│   │   ├── menu_main.xml                # Navigation menu
│   │   └── menu_add_note.xml            # Add note activity menu
│   │
│   ├── drawable/
│   │   └── rounded_background.xml       # Rounded background shape
│   │
│   ├── values/
│   │   ├── strings.xml                  # String resources
│   │   ├── colors.xml                   # Color definitions
│   │   └── themes.xml                   # App theme
│   │
│   └── xml/
│       ├── data_extraction_rules.xml
│       └── backup_rules.xml
│
├── AndroidManifest.xml                  # App configuration with permissions
│
└── build.gradle.kts                     # App-level dependencies

gradle/
└── libs.versions.toml                   # Centralized dependency versions
```

## 🔧 Dependencies

### Core Android
- **androidx.appcompat**: v1.7.1
- **com.google.android.material**: v1.13.0
- **androidx.activity**: v1.12.2
- **androidx.constraintlayout**: v2.2.1

### Networking
- **Retrofit**: v2.11.0 - HTTP client
- **retrofit-converter-gson**: v2.11.0 - JSON serialization
- **Gson**: v2.11.0 - JSON parser

### Database
- **androidx.room**: v2.6.1 - Local persistence
- **room-ktx**: v2.6.1 - Coroutines support

### Concurrency
- **kotlinx-coroutines-android**: v1.7.3
- **kotlinx-coroutines-core**: v1.7.3

### Background Tasks
- **androidx.work:work-runtime-ktx**: v2.9.1 - WorkManager

### Lifecycle
- **androidx.lifecycle:lifecycle-runtime-ktx**: v2.8.7
- **androidx.lifecycle:lifecycle-viewmodel-ktx**: v2.8.7

## 🚀 Key Features

### 1. **Quotes Feature**
- Fetch quotes from https://api.quotable.io/ API
- Display in paginated RecyclerView
- Auto-load more quotes on scroll
- Shows quote text and author

### 2. **Notes Feature**
- Create new notes with title and description
- Edit existing notes
- Delete notes
- Automatic timestamp capture (yyyy-MM-dd HH:mm:ss format)
- Display creation date in list
- Persistent storage in Room database

### 3. **Daily Notifications**
- WorkManager schedules background task
- Fetches random quote from API
- Sends push notification with quote content
- Notification channels for Android 8.0+
- Can be toggled on/off from Settings

### 4. **Settings**
- Toggle notification service on/off
- Uses SharedPreferences for persistence
- Changes take effect immediately

## 🔌 API Integration

### Quotable API (https://api.quotable.io/)
**Endpoints Used:**
- `GET /quotes?limit=10&skip=0` - Fetch paginated quotes
- `GET /random` - Fetch a random quote

**Response Model:**
```json
{
  "_id": "string",
  "content": "Quote text",
  "author": "Author name",
  "tags": ["tag1", "tag2"],
  "authorSlug": "author-slug",
  "length": 123,
  "dateAdded": "2023-01-01",
  "dateModified": "2023-01-01"
}
```

## 💾 Database Schema

### Notes Table
```sql
CREATE TABLE notes (
  id TEXT PRIMARY KEY,
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  createdAt TEXT NOT NULL,
  updatedAt TEXT NOT NULL
)
```

## 🔐 Permissions

```xml
<!-- Internet access for API calls -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Post notifications (Android 13+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## 📝 Usage Guide

### Viewing Quotes
1. Tap "View Quotes" on main screen
2. Scroll through quotes from the API
3. New quotes load automatically as you scroll
4. Quotes display text and author information

### Managing Notes
1. Tap "My Notes" on main screen
2. Tap FAB (+) to create new note
3. Enter title and description
4. Tap "Save" to store note with timestamp
5. Tap a note to edit it
6. Use menu to delete notes

### Configuring Notifications
1. Tap "Settings" on main screen
2. Toggle "Enable Daily Notifications" switch
3. When enabled, notifications are scheduled every 15 minutes
4. Each notification contains a random quote
5. Settings persist when app is closed

## 🛠️ Building & Running

### Prerequisites
- Android Studio Flamingo or newer
- Android SDK 24+ (API level 24+)
- Kotlin support enabled

### Build Steps
1. Clone/import project into Android Studio
2. Gradle will automatically download dependencies from `libs.versions.toml`
3. Build → Make Project
4. Run → Select emulator or device

### Running Tests
```bash
./gradlew test          # Unit tests
./gradlew connectedAndroidTest  # Instrumented tests
```

## 🎨 UI/UX Features

- **Material Design 3**: Modern UI components
- **Card Views**: Quotes and notes displayed in material cards
- **Responsive Layouts**: Works on different screen sizes
- **Toolbar Navigation**: Back buttons and activity titles
- **Floating Action Button**: Quick access to add notes
- **Switch Widget**: Easy toggle for notifications
- **EditText with Labels**: Material text input fields
- **Color Scheme**: Professional color scheme with proper contrast

## 🔄 Data Flow

```
API (Quotable)
    ↓
RetrofitClient → QuotesApiService
    ↓
QuotesActivity → QuotesAdapter → UI

Room Database
    ↓
NoteDao ↔ AddNoteActivity/NotesActivity
    ↓
NotesAdapter → UI

WorkManager
    ↓
DailyQuoteWorker → Fetch Quote
    ↓
NotificationManager → Push Notification
    ↓
User Phone

SharedPreferences
    ↓
PreferencesManager ↔ SettingsActivity
    ↓
Notification State
```

## 🐛 Error Handling

- **Network Errors**: Toast messages inform users of API failures
- **Database Errors**: Try-catch blocks prevent crashes
- **Empty Fields**: Validation prevents saving empty notes
- **Coroutine Cancellation**: Proper lifecycle management prevents memory leaks

## 📱 Tested Configurations

- **Min SDK**: Android 5.0 (API 24)
- **Target SDK**: Android 15 (API 36)
- **Languages**: Kotlin, XML
- **Screen Sizes**: Phone (4.5" - 6.7")

## 🎓 Academic Requirements

This project satisfies all coursework requirements:
- ✅ Multiple Activities with Intent navigation
- ✅ RecyclerView for data display
- ✅ API integration (Retrofit + JSON)
- ✅ Android Menus
- ✅ Local Storage (Room Database)
- ✅ Background Services (WorkManager)
- ✅ Shared Preferences
- ✅ Timestamps and CRUD operations
- ✅ Push notifications
- ✅ Professional code structure and best practices

## 📚 References

- [Android Developers - Room Database](https://developer.android.com/training/data-storage/room)
- [Android Developers - WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Android Developers - Retrofit](https://square.github.io/retrofit/)
- [Quotable API Documentation](https://github.com/lukePeavey/quotable)

---

**Created**: December 27, 2025
**Version**: 1.0
**Status**: Production Ready ✅
