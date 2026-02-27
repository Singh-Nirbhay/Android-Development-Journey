# Application Class & Context

> The foundation of every Android app - understand Context and create global app-level initialization

---

## 🤔 What is it?

**Application class** is a singleton that lives for your entire app's lifecycle - created before any Activity. **Context** is your gateway to Android resources, preferences, databases, and system services.

Think of Application as your app's "main office" that opens first and closes last. Context is like your ID badge - it gives you access to everything in the building (resources, files, services).

---

## 💡 Why do we need it in Android?

Every Android app needs one-time initialization: setting up crash reporting, initializing databases, configuring libraries. The Application class is THE place to do this - it runs before any screen appears.

```kotlin
// Without custom Application - initialization scattered everywhere 😫
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDatabase()  // Initialized in every Activity?
        setupAnalytics() // Multiple times?
    }
}

// With custom Application - initialize ONCE! 🎉
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initDatabase()    // Runs once, before any Activity
        setupAnalytics()  // Available to entire app
    }
}
```

Context lets you access resources (`getString(R.string.app_name)`), SharedPreferences, databases, and system services - you'll use it everywhere in Android.

---

## 📌 Key Concepts

- **Application Class**: Singleton base class for app-wide state, created before Activities/Services
- **Context**: Abstract class providing access to app-specific resources and classes
- **Application Context**: Lives as long as the app (safe to store globally)
- **Activity Context**: Tied to Activity lifecycle (don't store globally - memory leaks!)
- **SharedPreferences**: Key-value storage for simple data (user settings, tokens)
- **lateinit Context**: Store Application context in singletons safely
- **Memory Leak**: Holding Activity context in long-lived objects prevents garbage collection

---

## ✍️ Syntax

```kotlin
// ============================================
// CUSTOM APPLICATION CLASS
// ============================================

class MyApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // This runs ONCE when app starts (before any Activity)
        println("App is starting!")
        
        // Initialize app-level singletons
        AppInfo.initialize(applicationContext)
        
        // Setup libraries (analytics, crash reporting, etc.)
        // Firebase.initialize(this)
        // Timber.plant(Timber.DebugTree())
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        println("System is low on memory!")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        // NOTE: This is NEVER called on real devices
        // Only called in emulated processes
    }
}

// ============================================
// REGISTER IN MANIFEST
// ============================================
/*
<application
    android:name=".MyApp"
    ...>
</application>
*/

// ============================================
// SINGLETON WITH CONTEXT
// ============================================

object AppInfo {
    private lateinit var appContext: Context
    
    fun initialize(context: Context) {
        // Store APPLICATION context (not Activity!)
        appContext = context.applicationContext
    }
    
    fun getAppName(): String {
        check(::appContext.isInitialized) {
            "AppInfo not initialized"
        }
        return appContext.getString(R.string.app_name)
    }
    
    fun getVersionName(): String {
        val packageInfo = appContext.packageManager
            .getPackageInfo(appContext.packageName, 0)
        return packageInfo.versionName
    }
}

// ============================================
// SHAREDPREFERENCES WRAPPER
// ============================================

class UserPreferences(context: Context) {
    
    private companion object {
        const val PREFS_NAME = "user_prefs"
        const val KEY_USER_NAME = "user_name"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    // Use application context to avoid memory leaks
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    // Save string
    fun saveUserName(name: String) {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .apply()  // async save
    }
    
    // Get string with default
    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "Guest") ?: "Guest"
    }
    
    // Save boolean
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            .apply()
    }
    
    // Get boolean
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    // Clear specific key
    fun clearUserName() {
        prefs.edit()
            .remove(KEY_USER_NAME)
            .apply()
    }
    
    // Clear all data
    fun clearAll() {
        prefs.edit()
            .clear()
            .apply()
    }
}

// ============================================
// USAGE IN ACTIVITY
// ============================================

class MainActivity : AppCompatActivity() {
    
    private lateinit var userPrefs: UserPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Use applicationContext (not 'this')
        userPrefs = UserPreferences(applicationContext)
        
        // Test preferences
        val userName = userPrefs.getUserName()
        println("Welcome, $userName!")
        
        // Save new name
        userPrefs.saveUserName("John Doe")
        
        // Get app info
        val appName = AppInfo.getAppName()
        println("App: $appName")
    }
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Using Activity context in long-lived objects

```kotlin
// Wrong - MEMORY LEAK! Activity can't be garbage collected
object UserManager {
    lateinit var context: Context
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserManager.context = this  // 💥 Leaks entire Activity!
    }
}
```

### ✅ Correct way

```kotlin
// Right - use Application context
object UserManager {
    lateinit var appContext: Context
}

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UserManager.appContext = applicationContext  // ✅ Safe
    }
}
```

---

### ❌ Mistake 2: Forgetting to register Application class in manifest

```kotlin
// Created custom Application class...
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        println("Starting...")  // Never prints!
    }
}

// But forgot to register in AndroidManifest.xml
// <application> tag doesn't have android:name attribute
```

### ✅ Correct way

```xml
<!-- AndroidManifest.xml -->
<application
    android:name=".MyApp"
    ...>
</application>
```

---

### ❌ Mistake 3: Not using `.applicationContext` with SharedPreferences

```kotlin
class UserPreferences(private val context: Context) {
    // Wrong - if context is Activity, memory leak!
    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
}

// Usage with Activity context
val prefs = UserPreferences(this)  // 'this' is Activity
```

### ✅ Correct way

```kotlin
class UserPreferences(context: Context) {
    // Right - always use applicationContext
    private val prefs = context.applicationContext
        .getSharedPreferences("prefs", Context.MODE_PRIVATE)
}

// Safe with any context
val prefs = UserPreferences(this)
val prefs2 = UserPreferences(applicationContext)  // Both safe
```

---

### ❌ Mistake 4: Using `commit()` instead of `apply()`

```kotlin
// Wrong - blocks UI thread!
prefs.edit()
    .putString("name", "John")
    .commit()  // Synchronous write - freezes UI
```

### ✅ Correct way

```kotlin
// Right - asynchronous save
prefs.edit()
    .putString("name", "John")
    .apply()  // Non-blocking

// Use commit() ONLY if you need immediate confirmation
val success = prefs.edit()
    .putString("critical_data", data)
    .commit()  // Returns true/false
```

---

### ❌ Mistake 5: Not checking `lateinit` initialization

```kotlin
object AppInfo {
    lateinit var appContext: Context
    
    fun getAppName(): String {
        // Wrong - crashes if not initialized!
        return appContext.getString(R.string.app_name)
    }
}
```

### ✅ Correct way

```kotlin
object AppInfo {
    private lateinit var appContext: Context
    
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
    
    fun getAppName(): String {
        // Check if initialized
        check(::appContext.isInitialized) {
            "AppInfo not initialized. Call initialize() in Application.onCreate()"
        }
        return appContext.getString(R.string.app_name)
    }
}
```

---

## 📊 Context Types Comparison

| Context Type | Lifecycle | Safe to Store? | Use For |
|-------------|-----------|----------------|---------|
| `Application` | Entire app | ✅ Yes | Singletons, long-lived objects |
| `Activity` | Single screen | ❌ No | UI operations, dialogs |
| `Service` | Background task | ⚠️ Depends | Service-specific operations |

---

## 🧠 Application Lifecycle

```kotlin
// App launch sequence:
// 1. MyApp.onCreate() - App starts
// 2. MainActivity.onCreate() - First screen appears
// 3. User navigates through app
// 4. App goes to background
// 5. System kills app (MyApp destroyed)

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Runs ONCE when app process starts
        println("App starting...")
    }
    
    override fun onLowMemory() {
        super.onLowMemory()
        // System is running low on memory
        // Clear caches, release resources
    }
}
```

---

## 🎯 Mini Task

**What we're building:**  
A complete user preferences system with custom Application class initialization, singleton app info manager, and SharedPreferences wrapper - production-ready pattern.

**What you'll learn:**
- How to create and register a custom Application class
- How to store Application context safely in singletons
- How to use SharedPreferences for persistent data
- How to avoid memory leaks with proper Context usage
- Real pattern used in every production Android app

---

## 📚 Quick Recap

- **Application class** runs before Activities - perfect for one-time initialization
- Register custom Application in **AndroidManifest.xml** with `android:name`
- **Application context** is safe to store globally - **Activity context** is NOT
- Use **`context.applicationContext`** to avoid memory leaks
- **SharedPreferences** stores key-value data - use `apply()` not `commit()`
- Store Application context in singletons using `lateinit var`
- Always check `::context.isInitialized` before using lateinit context
- Common uses: analytics, crash reporting, database setup, global configs

**Next Topic:** Activities & Intents (navigating between screens, passing data)