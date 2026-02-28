import javax.naming.Context

// =============================
// MyApp.kt
// =============================
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        println("🚀 App is starting!")

        // Initialize singletons
        AppInfo.initialize(applicationContext)
    }
}

// =============================
// UserPreferences.kt
// =============================
/**
 * Manages user preferences using SharedPreferences.
 *
 * @param context Pass applicationContext to avoid memory leaks
 */
class UserPreferences(context: Context) {

    private companion object {
        const val PREFS_NAME = "user_prefs"
        const val KEY_USER_NAME = "user_name"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserName(name: String) {
        prefs.edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "Guest") ?: "Guest"
    }

    fun clearUserName() {
        prefs.edit()
            .remove(KEY_USER_NAME)
            .apply()
    }
}

// =============================
// AppInfo.kt
// =============================
object AppInfo {

    private lateinit var appContext: Context

    /**
     * Initialize AppInfo with application context.
     * Call this in Application.onCreate()
     */
    fun initialize(context: Context) {
        // Prevent double initialization
        if (::appContext.isInitialized) {
            println("⚠️ WARNING: AppInfo already initialized")
            return
        }
        appContext = context.applicationContext
        println("✅ AppInfo initialized")
    }

    /**
     * Get app name from string resources
     */
    fun getAppName(): String {
        check(::appContext.isInitialized) {
            "AppInfo not initialized. Call initialize() in Application.onCreate()"
        }
        return appContext.getString(R.string.app_name)
    }

    /**
     * Check if app is in debug mode
     */
    fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }
}

// =============================
// MainActivity.kt
// =============================
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Test our code
        demonstrateAppInfo()
        demonstrateUserPreferences()
    }

    private fun demonstrateAppInfo() {
        println("\n=== Testing AppInfo ===")

        val appName = AppInfo.getAppName()
        println("📱 App name: $appName")

        val isDebug = AppInfo.isDebugBuild()
        println("🔧 Debug mode: $isDebug")
    }

    private fun demonstrateUserPreferences() {
        println("\n=== Testing UserPreferences ===")

        val prefs = UserPreferences(applicationContext)

        // Check initial value
        println("👤 Initial user: ${prefs.getUserName()}")  // "Guest"

        // Save name
        prefs.saveUserName("Nirbhay")
        println("👤 After save: ${prefs.getUserName()}")  // "Nirbhay"

        // Save different name
        prefs.saveUserName("John")
        println("👤 After update: ${prefs.getUserName()}")  // "John"

        // Clear
        prefs.clearUserName()
        println("👤 After clear: ${prefs.getUserName()}")  // "Guest"
    }
}

// =============================
// AndroidManifest.xml
// =============================
/*
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp">

    <application
        android:name=".MyApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.MyApp">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
*/
