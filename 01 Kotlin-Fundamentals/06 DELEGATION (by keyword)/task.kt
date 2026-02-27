//🏗️ BUILD CHALLENGE #6
//Create a Settings Manager with Delegation:
//
//Step 1: Create an interface Settings:
//
//    Kotlin
//
//interface Settings {
//    fun getString(key: String): String?
//    fun putString(key: String, value: String)
//    fun getInt(key: String): Int
//    fun putInt(key: String, value: Int)
//}
//Step 2: Create InMemorySettings class that implements Settings:
//
//Uses a MutableMap<String, Any> internally
//Implements all methods
//Step 3: Create LoggingSettings class:
//
//    Takes Settings as constructor parameter
//Delegates to it using by keyword
//Overrides ALL methods to print log before calling delegate
//Step 4: Create a class UserPreferences:
//
//    Has property fontSize: Int using observable delegate (prints when changed)
//Has property theme: String using lazy delegate (prints "Loading theme..." when first accessed)
//Step 5: In main():
//
//Create InMemorySettings
//Wrap it with LoggingSettings
//Put and get some values
//Create UserPreferences and test fontSize and theme
import kotlin.properties.Delegates

// ----------------------------
// Interface
// ----------------------------
interface Settings {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun getInt(key: String): Int
    fun putInt(key: String, value: Int)
}

// ----------------------------
// InMemorySettings — Silent storage
// ----------------------------
class InMemorySettings : Settings {
    private val storage = mutableMapOf<String, Any>()

    override fun putInt(key: String, value: Int) {
        storage[key] = value
    }

    override fun getInt(key: String) = storage[key] as? Int ?: 0

    override fun putString(key: String, value: String) {
        storage[key] = value
    }

    override fun getString(key: String) = storage[key] as? String
}

// ----------------------------
// LoggingSettings — Decorator with logging
// ----------------------------
class LoggingSettings(
    private val settings: Settings
) : Settings by settings {

    override fun putInt(key: String, value: Int) {
        println("📝 PUT Int: $key = $value")
        settings.putInt(key, value)
    }

    override fun putString(key: String, value: String) {
        println("📝 PUT String: $key = \"$value\"")
        settings.putString(key, value)
    }

    override fun getInt(key: String): Int {
        val value = settings.getInt(key)
        println("📖 GET Int: $key → $value")
        return value
    }

    override fun getString(key: String): String? {
        val value = settings.getString(key)
        println("📖 GET String: $key → ${value ?: "null"}")
        return value
    }
}

// ----------------------------
// UserPreferences — Using property delegates
// ----------------------------
class UserPreferences {

    var fontSize: Int by Delegates.observable(14) { _, oldValue, newValue ->
        println("🔤 fontSize: $oldValue → $newValue")
    }

    val theme: String by lazy {
        println("🎨 Loading theme...")
        "Dark Mode"
    }
}

// ----------------------------
// Main
// ----------------------------
fun main() {
    println("=== SETTINGS TEST ===\n")

    val inMemorySettings = InMemorySettings()
    val loggingSettings = LoggingSettings(inMemorySettings)

    loggingSettings.putInt("sound", 0)
    loggingSettings.putString("brightness", "100%")

    println()

    loggingSettings.getInt("sound")
    loggingSettings.getString("brightness")

    println("\n=== USER PREFERENCES TEST ===\n")

    val userPreferences = UserPreferences()

    println("Changing fontSize...")
    userPreferences.fontSize = 22
    userPreferences.fontSize = 18

    println()

    println("First theme access:")
    println("Theme: ${userPreferences.theme}")

    println("\nSecond theme access:")
    println("Theme: ${userPreferences.theme}")
}













