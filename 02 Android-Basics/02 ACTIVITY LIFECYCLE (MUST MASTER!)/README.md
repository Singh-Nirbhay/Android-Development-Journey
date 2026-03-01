# Activity Lifecycle (MUST MASTER!)

> The heartbeat of every Android screen - master these 7 methods to build robust apps

---

## 🤔 What is it?

**Activity lifecycle** is the series of states an Activity goes through from creation to destruction. Android calls specific callback methods (`onCreate`, `onStart`, `onResume`, etc.) as the Activity transitions between states.

Think of it like a day in your life: wake up (onCreate), start your day (onStart), actively working (onResume), take a break (onPause), go home (onStop), sleep (onDestroy). Android manages your app's screens the same way.

---

## 💡 Why do we need it in Android?

Android can destroy your Activity at ANY time to save memory (user rotates screen, presses home, low memory). If you don't handle lifecycle properly, you lose data, leak resources, or crash.

```kotlin
// Without lifecycle handling - data loss! 😫
class MainActivity : ComponentActivity() {
    private var userInput = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // User types essay, rotates screen → onCreate called again
        // userInput is reset to "" → ALL DATA LOST!
    }
}

// With lifecycle handling - data survives! 🎉
class MainActivity : ComponentActivity() {
    private var userInput by mutableStateOf("")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore saved data
        userInput = savedInstanceState?.getString("input") ?: ""
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save data before destruction
        outState.putString("input", userInput)
    }
}
```

Every Android developer MUST master this - it's tested in interviews and critical for production apps.

---

## 📌 Key Concepts

- **onCreate()**: Activity is being created - initialize UI, restore state
- **onStart()**: Activity is becoming visible - prepare resources
- **onResume()**: Activity is in foreground and interactive - start animations, sensors
- **onPause()**: Activity is losing focus - pause heavy operations
- **onStop()**: Activity is no longer visible - release resources
- **onDestroy()**: Activity is being destroyed - final cleanup
- **onRestart()**: Activity is restarting from stopped state
- **onSaveInstanceState()**: Save temporary state before destruction
- **Configuration Change**: Screen rotation, language change triggers destroy → recreate

---

## ✍️ The Lifecycle Flow

```
┌─────────────────────────────────────────────┐
│          Activity Launched                  │
└─────────────────┬───────────────────────────┘
                  ▼
            ┌──────────┐
            │ onCreate │ ← Create UI, restore state
            └─────┬────┘
                  ▼
            ┌──────────┐
            │ onStart  │ ← Becoming visible
            └─────┬────┘
                  ▼
            ┌──────────┐
            │ onResume │ ← In foreground (user can interact)
            └─────┬────┘
                  │
    ┌─────────────┴─────────────┐
    │  Activity Running         │
    │  (User interacts)         │
    └─────────────┬─────────────┘
                  ▼
    ┌─────────────────────────┐
    │   Another Activity      │
    │   comes to foreground   │
    └─────────┬───────────────┘
              ▼
        ┌──────────┐
        │ onPause  │ ← Partially obscured
        └─────┬────┘
              ▼
        ┌──────────┐
        │ onStop   │ ← No longer visible
        └─────┬────┘
              │
    ┌─────────┴──────────┐
    │                    │
    ▼                    ▼
┌──────────┐      ┌────────────┐
│ onRestart│      │ onDestroy  │ ← Destroyed
└─────┬────┘      └────────────┘
      │
      └──► Back to onStart
```

---

## ✍️ Syntax

```kotlin
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "LifecycleDemo"
        private const val KEY_COUNTER = "counter_key"
    }
    
    // State that survives recomposition but NOT configuration changes
    private var counter by mutableStateOf(0)
    
    // ============================================
    // LIFECYCLE METHOD 1: onCreate
    // ============================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "onCreate - Time: ${System.currentTimeMillis()}")
        
        // Restore saved state
        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt(KEY_COUNTER, 0)
            Log.d(TAG, "Restored counter: $counter")
        }
        
        // Increment on each creation
        counter++
        
        // Set up UI (Compose)
        setContent {
            AndroidDevJourneyTheme {
                CounterScreen(
                    counter = counter,
                    onIncrement = { 
                        counter++
                        Log.d(TAG, "Counter incremented: $counter")
                    }
                )
            }
        }
    }
    
    // ============================================
    // LIFECYCLE METHOD 2: onStart
    // ============================================
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart - Activity becoming visible")
        
        // Start preparing resources (load data, connect to services)
        // Don't start expensive operations yet (wait for onResume)
    }
    
    // ============================================
    // LIFECYCLE METHOD 3: onResume
    // ============================================
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume - Activity in foreground, counter: $counter")
        
        // Start animations, sensors, location updates
        // Register broadcast receivers
        // Resume video playback
    }
    
    // ============================================
    // LIFECYCLE METHOD 4: onPause
    // ============================================
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause - Activity losing focus")
        
        // Pause animations, video playback
        // Don't save data here (might not be called in extreme cases)
        // Keep it fast - delays next Activity from showing
    }
    
    // ============================================
    // LIFECYCLE METHOD 5: onStop
    // ============================================
    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop - Activity no longer visible")
        
        // Release resources (unregister receivers, stop location)
        // Save draft data to database
        // Stop network requests
    }
    
    // ============================================
    // LIFECYCLE METHOD 6: onRestart
    // ============================================
    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart - Activity restarting from stopped state")
        
        // Called when coming back from another Activity
        // Follows with onStart → onResume
    }
    
    // ============================================
    // LIFECYCLE METHOD 7: onDestroy
    // ============================================
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy - Activity being destroyed")
        
        // Final cleanup (close database connections, cancel coroutines)
        // Check isFinishing to distinguish user-initiated close vs config change
        if (isFinishing) {
            Log.d(TAG, "User is leaving - final cleanup")
        } else {
            Log.d(TAG, "Configuration change - will be recreated")
        }
    }
    
    // ============================================
    // SAVE STATE BEFORE DESTRUCTION
    // ============================================
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState - Saving counter: $counter")
        
        // Save temporary state (survives config changes, NOT app kill)
        outState.putInt(KEY_COUNTER, counter)
        outState.putString("user_input", "Hello")
        
        // For permanent storage, use ViewModel or Room database
    }
}

// ============================================
// COMPOSABLE UI
// ============================================
@Composable
fun CounterScreen(counter: Int, onIncrement: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Counter: $counter",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onIncrement) {
            Text("Increment")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Rotate screen or press home to test lifecycle",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Not calling `super` methods

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    // Wrong - forgot super.onCreate()!
    setContent { /* UI */ }  // 💥 Crash!
}
```

### ✅ Correct way

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)  // ✅ ALWAYS call super first
    setContent { /* UI */ }
}
```

---

### ❌ Mistake 2: Doing heavy work in `onCreate`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Wrong - blocks UI thread!
    val data = loadDataFromNetwork()  // Takes 5 seconds
    setContent { /* UI */ }  // Delayed!
}
```

### ✅ Correct way

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    setContent { /* UI */ }  // Show UI immediately
    
    // Load data in coroutine
    lifecycleScope.launch {
        val data = loadDataFromNetwork()  // Background thread
        updateUI(data)
    }
}
```

---

### ❌ Mistake 3: Not saving state in `onSaveInstanceState`

```kotlin
class MainActivity : ComponentActivity() {
    private var userText by mutableStateOf("")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // User types essay → rotates screen → ALL TEXT LOST!
        setContent {
            TextField(value = userText, onValueChange = { userText = it })
        }
    }
}
```

### ✅ Correct way

```kotlin
class MainActivity : ComponentActivity() {
    private var userText by mutableStateOf("")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore saved text
        userText = savedInstanceState?.getString("text") ?: ""
        
        setContent {
            TextField(value = userText, onValueChange = { userText = it })
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("text", userText)  // ✅ Survives rotation
    }
}
```

---

### ❌ Mistake 4: Starting sensors in `onCreate` instead of `onResume`

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Wrong - sensor keeps running even when app is in background!
    startLocationUpdates()
}
```

### ✅ Correct way

```kotlin
override fun onResume() {
    super.onResume()
    startLocationUpdates()  // Start when visible
}

override fun onPause() {
    super.onPause()
    stopLocationUpdates()  // Stop when hidden
}
```

---

### ❌ Mistake 5: Leaking resources in `onDestroy`

```kotlin
class MainActivity : ComponentActivity() {
    private val database = openDatabase()
    
    override fun onDestroy() {
        super.onDestroy()
        // Wrong - forgot to close database!
        // Resource leak!
    }
}
```

### ✅ Correct way

```kotlin
class MainActivity : ComponentActivity() {
    private val database = openDatabase()
    
    override fun onDestroy() {
        super.onDestroy()
        database.close()  // ✅ Clean up resources
    }
}
```

---

## 📊 Lifecycle Scenarios

### Scenario 1: **Open App**
```
onCreate → onStart → onResume
```

### Scenario 2: **Press Home Button**
```
onPause → onStop
(Activity still exists in memory)
```

### Scenario 3: **Come Back to App**
```
onRestart → onStart → onResume
```

### Scenario 4: **Rotate Screen**
```
onPause → onStop → onSaveInstanceState → onDestroy
→ onCreate (new instance) → onStart → onResume
```

### Scenario 5: **Press Back Button**
```
onPause → onStop → onDestroy
(Activity removed from stack)
```

### Scenario 6: **Open Another Activity**
```
CurrentActivity: onPause
NewActivity: onCreate → onStart → onResume
CurrentActivity: onStop
```

---

## 🧠 What to Do in Each Method

| Method | What to Do | What NOT to Do |
|--------|-----------|----------------|
| `onCreate` | Set up UI, restore state, initialize variables | Heavy network/database operations |
| `onStart` | Register listeners, prepare resources | Start animations (wait for onResume) |
| `onResume` | Start animations, sensors, location updates | One-time initialization |
| `onPause` | Pause animations, video playback | Save critical data (use onStop) |
| `onStop` | Unregister listeners, save drafts | Assume it will be called (extreme cases) |
| `onDestroy` | Close connections, cancel coroutines | Save important data (might not be called) |

---

## 🎯 Mini Task

**What we're building:**  
A lifecycle logging app that tracks ALL lifecycle callbacks with timestamps, saves/restores counter state across configuration changes, and demonstrates proper resource management.

**What you'll learn:**
- How to override all 7 lifecycle methods
- How to use `Log.d()` for debugging (visible in Logcat)
- How to save/restore state with `onSaveInstanceState`
- How configuration changes (rotation) trigger destroy → recreate
- When to start/stop resources (sensors, animations, network)
- Production patterns for robust Android apps

---

## 📚 Quick Recap

- **onCreate**: Initialize UI, restore saved state - called once per instance
- **onStart**: Activity becoming visible - prepare resources
- **onResume**: Activity in foreground - start sensors, animations
- **onPause**: Activity losing focus - pause heavy operations (keep fast!)
- **onStop**: Activity no longer visible - release resources
- **onRestart**: Activity restarting from stopped state
- **onDestroy**: Final cleanup - close connections, cancel work
- **onSaveInstanceState**: Save temporary state before destruction (survives rotation)
- ALWAYS call `super.method()` FIRST in lifecycle callbacks
- Screen rotation triggers full destroy → recreate cycle
- Use `Log.d(TAG, message)` for debugging lifecycle flow

**Next Topic:** Intents & Navigation (moving between screens, passing data)