# Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### Step 1: Open Project in Android Studio
```
File → Open → Select DailyQuotes_1 folder
```

### Step 2: Wait for Gradle Sync
- Android Studio will automatically download all dependencies
- Check the bottom bar for sync status
- Wait until sync completes (may take 2-5 minutes)

### Step 3: Select Target Device
```
Select Emulator:
  - Tools → Device Manager
  - Select an Android 5.0+ emulator
  - Start the emulator

OR Connect Physical Device:
  - Enable Developer Mode (tap Build Number 7 times)
  - Enable USB Debugging
  - Connect via USB cable
```

### Step 4: Run the App
```
Click the green Run button (▶️) in toolbar
OR
Run → Run 'app'
```

### Step 5: Wait for App Installation
- Gradle will build and install APK
- App should launch automatically on device

---

## 🎯 Testing Each Feature

### Test Quotes Feature
1. **Launch app** → Tap "View Quotes"
2. **See quotes** → Should load from API
3. **Scroll down** → More quotes auto-load
4. **View menu** → Should show author name

### Test Notes Feature
1. **From main** → Tap "My Notes"
2. **Add note** → Tap FAB (+) button
3. **Fill form** → Title + Description
4. **Save** → Should appear in list with timestamp
5. **Edit note** → Tap any note in list
6. **Modify** → Change text and save
7. **Delete** → From edit screen, use menu

### Test Notifications
1. **Go to Settings** → Tap Settings button
2. **Enable notifications** → Toggle switch ON
3. **Verify** → Toast says "enabled"
4. **Wait** → After 15 minutes, notification should appear
5. **Disable** → Toggle OFF
6. **Verify** → Toast says "disabled"

### Test Persistence
1. **Add a note** → Fill and save
2. **Enable notifications** → Toggle ON
3. **Close app** → Kill app completely
4. **Reopen app** → Tap app icon
5. **Check Notes** → Note should still exist
6. **Check Settings** → Toggle should still be ON

---

## 📋 File Locations for Quick Reference

### Code Files (Main Logic)
```
app/src/main/java/com/example/dailyquotes_1/

UI Activities:
  ui/MainActivity.kt
  ui/QuotesActivity.kt
  ui/NotesActivity.kt
  ui/AddNoteActivity.kt
  ui/SettingsActivity.kt

Data Layer:
  data/model/Quote.kt
  data/model/Note.kt
  data/remote/QuotesApiService.kt
  data/remote/RetrofitClient.kt
  data/local/NoteDao.kt
  data/local/AppDatabase.kt
  data/local/DatabaseProvider.kt

Other Components:
  adapter/QuotesAdapter.kt
  adapter/NotesAdapter.kt
  workers/DailyQuoteWorker.kt
  utils/PreferencesManager.kt
```

### Layout Files
```
app/src/main/res/layout/

Activities:
  activity_main.xml
  activity_quotes.xml
  activity_notes.xml
  activity_add_note.xml
  activity_settings.xml

Items:
  item_quote.xml
  item_note.xml
```

### Resource Files
```
app/src/main/res/

Strings:
  values/strings.xml

Menus:
  menu/menu_main.xml
  menu/menu_add_note.xml

Drawables:
  drawable/rounded_background.xml
```

### Configuration
```
App-level:
  app/build.gradle.kts

Version Management:
  gradle/libs.versions.toml

Manifest:
  app/src/main/AndroidManifest.xml
```

---

## 🔧 Build Configuration

### Gradle Files to Know

**libs.versions.toml**
```
Location: gradle/libs.versions.toml
Purpose: Centralized dependency versions
Edit when: Updating library versions
```

**app/build.gradle.kts**
```
Location: app/build.gradle.kts
Purpose: App-specific gradle config
Edit when: Adding new dependencies
```

### Dependency Resolution

When you open project:
1. Android Studio reads `libs.versions.toml`
2. Downloads all libraries from Maven Central
3. Builds cache in `~/.gradle` folder
4. Your app can use all libraries

---

## 📱 Emulator Tips

### Create Emulator for First Time
```
Device Manager → Create Device
- Select device size (Pixel 4a recommended)
- Select API level (Android 12 or higher recommended)
- Click Create
- Wait 1-2 minutes
- Start emulator
```

### Speed Up Emulator
- Give more RAM: Device Manager → Edit → Advanced
- Set RAM to 4GB minimum
- Close other apps on your computer

### Run App on Emulator
```
1. Start emulator first
2. Click Run button
3. Select emulator from dialog
4. App installs and starts
```

---

## 🐛 Common Issues & Solutions

### Issue: Gradle Sync Fails
```
Solution:
1. File → Sync Now
2. If still fails: File → Invalidate Caches → Invalidate and Restart
3. Wait for full index rebuild
```

### Issue: "Cannot find R class"
```
Solution:
1. Build → Clean Project
2. Build → Rebuild Project
3. File → Sync Now
4. Wait for rebuild
```

### Issue: App Crashes on Launch
```
Solution:
1. Check Logcat (bottom panel)
2. Look for red error messages
3. Common causes:
   - Database initialization fail
   - Missing manifest activity
   - Incorrect class name
4. Fix and rebuild
```

### Issue: API Calls Fail
```
Solution:
1. Check internet connection on device
2. Verify emulator has internet access
3. Check API URL in RetrofitClient.kt
4. Test API in browser: https://api.quotable.io/random
```

### Issue: Notifications Don't Appear
```
Solution:
1. Check if notifications enabled in settings
2. Ensure WorkManager is scheduled
3. Check device notification settings
4. Wait at least 15 minutes for first notification
5. Check logcat for errors
```

### Issue: Notes Don't Save
```
Solution:
1. Check logcat for database errors
2. Ensure DatabaseProvider is working
3. Check AddNoteActivity.kt save logic
4. Verify Room dependencies in build.gradle.kts
```

---

## 🔄 Making Your First Changes

### Change Quote API Limit (Show More Quotes)
**File**: `app/src/main/java/com/example/dailyquotes_1/ui/QuotesActivity.kt`

**Find**:
```kotlin
limit = pageSize  // Currently 10
```

**Change to**:
```kotlin
limit = 20  // Show 20 quotes per page
```

**Rebuild**: Build → Rebuild Project

---

### Change Notification Frequency
**File**: `app/src/main/java/com/example/dailyquotes_1/ui/SettingsActivity.kt`

**Find**:
```kotlin
val dailyQuoteWork = PeriodicWorkRequestBuilder<DailyQuoteWorker>(
    15,
    TimeUnit.MINUTES
)
```

**Change 15 to** (in minutes):
```kotlin
60,  // Once per hour
```

**Or change TimeUnit**:
```kotlin
1,
TimeUnit.DAYS  // Once per day
```

---

### Change Date Format in Notes
**File**: `app/src/main/java/com/example/dailyquotes_1/ui/AddNoteActivity.kt`

**Find**:
```kotlin
val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
```

**Change format string**:
```kotlin
val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
```

**Rebuild and test**

---

## ✅ Pre-Launch Checklist

Before submitting for coursework:

### Code Quality
- [ ] All files have proper package declarations
- [ ] No unused imports (use Code → Optimize Imports)
- [ ] No error squiggles in editor
- [ ] Builds successfully without warnings

### Features
- [ ] Quotes load from API
- [ ] Pagination works
- [ ] Notes CRUD operations work
- [ ] Timestamps auto-capture
- [ ] Notifications toggle works
- [ ] Settings persist

### Testing
- [ ] Tested on Android 5.0+ device
- [ ] Tested with no internet (shows error)
- [ ] Tested with valid internet (loads data)
- [ ] Orientation changes don't crash
- [ ] App survives task kill

### Documentation
- [ ] README.md complete
- [ ] Code comments where needed
- [ ] Activity names match manifest
- [ ] No hardcoded strings (use strings.xml)

### Submission
- [ ] Delete build/ folder to reduce size
- [ ] Commit to git with meaningful messages
- [ ] Create APK: Build → Build Bundle(s)/APK(s) → Build APK(s)
- [ ] APK located: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📞 Need Help?

### Check These First
1. **Logcat** - Shows all errors and logs
2. **Build output** - Shows compilation issues
3. **README.md** - Project overview
4. **IMPLEMENTATION_NOTES.md** - Detailed guide

### Common Error Messages

**"Activity not declared in manifest"**
- Check AndroidManifest.xml
- Verify activity full class name
- Rebuild project

**"Cannot resolve class..."**
- Check package declarations
- Check imports at top of file
- Rebuild project

**"Cannot find symbol..."**
- Missing import
- Typo in class name
- Wrong package path

---

## 🎓 Learning Resources

- **Android Developer Docs**: developer.android.com
- **Kotlin Documentation**: kotlinlang.org
- **Retrofit Guide**: square.github.io/retrofit
- **Room Database**: developer.android.com/training/data-storage/room
- **WorkManager**: developer.android.com/topic/libraries/architecture/workmanager

---

## ⏱️ Estimated Build Times

- First build (clean): 2-5 minutes
- Incremental build: 30-60 seconds
- Gradle sync: 1-3 minutes
- Emulator startup: 30 seconds - 2 minutes
- App installation: 10-30 seconds

---

## 🎉 Congratulations!

You now have a fully functional Android application with:
- ✅ Multiple Activities
- ✅ RecyclerView lists
- ✅ API integration
- ✅ Local database
- ✅ Background notifications
- ✅ User preferences
- ✅ Professional UI/UX

### Next Steps (Optional Enhancements)

1. Add offline caching for quotes
2. Implement note search functionality
3. Add note categories/tags
4. Create favorites feature
5. Add dark mode support
6. Cloud backup integration
7. Note sharing functionality

---

**Ready to Launch!** 🚀

Good luck with your Mobile Programming coursework!

---

*Last Updated: December 27, 2025*
*Status: Production Ready*
