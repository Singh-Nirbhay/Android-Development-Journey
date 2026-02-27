# Delegation (by keyword)

> Reuse behavior without inheritance by letting another object do the work for you

---

## 🤔 What is it?

**Delegation** is a design pattern where one object hands off tasks to another object. In Kotlin, the `by` keyword makes this automatic - you can implement an interface by delegating ALL method calls to another object, or use built-in property delegates like `lazy` and `observable`.

Think of it like hiring an assistant. Instead of doing everything yourself (inheritance), you delegate tasks to someone who already knows how to do them.

---

## 💡 Why do we need it in Android?

In Android, you often need to wrap existing functionality with extra behavior (logging, caching, analytics) without modifying the original class. Delegation lets you do this cleanly.

```kotlin
// Without delegation - must manually forward EVERY method 😫
class LoggingSettings(private val delegate: Settings) : Settings {
    override fun getString(key: String): String? {
        println("Getting $key")
        return delegate.getString(key) // Manual forwarding
    }
    override fun putString(key: String, value: String) {
        println("Putting $key")
        delegate.putString(key, value) // Manual forwarding
    }
    // ... 10 more methods to forward manually 😵
}

// With delegation - automatic forwarding! 🎉
class LoggingSettings(delegate: Settings) : Settings by delegate {
    // All methods automatically forwarded!
    // Only override what you want to customize
}
```

Property delegates like `lazy` and `observable` are used everywhere in Android for ViewModel initialization, SharedPreferences, and state management.

---

## 📌 Key Concepts

- **Class Delegation (`by`)**: Implement an interface by forwarding all calls to another object automatically
- **Property Delegation**: Let another object handle a property's get/set behavior
- **`lazy`**: Initialize a property only when first accessed (great for expensive operations)
- **`observable`**: React to property changes with a callback (great for UI updates)
- **Decorator Pattern**: Wrap an object to add behavior without changing the original
- **Composition over Inheritance**: Use delegation to combine behaviors flexibly

---

## ✍️ Syntax

```kotlin
// ============================================
// CLASS DELEGATION - implement interface via another object
// ============================================

interface Printer {
    fun print(message: String)
}

class ConsolePrinter : Printer {
    override fun print(message: String) = println(message)
}

// Delegates ALL Printer methods to 'printer' automatically
class LoggingPrinter(printer: Printer) : Printer by printer {
    // Override only what you need
    override fun print(message: String) {
        println("[LOG] Printing...")
        // Call original implementation somehow? Need reference!
    }
}

// Better - keep reference to delegate
class LoggingPrinter(private val delegate: Printer) : Printer by delegate {
    override fun print(message: String) {
        println("[LOG] Printing...")
        delegate.print(message) // Call original
    }
}

// ============================================
// PROPERTY DELEGATION - lazy & observable
// ============================================

import kotlin.properties.Delegates

class UserPreferences {
    // lazy - computed only on first access
    val theme: String by lazy {
        println("Loading theme...") // Runs only once!
        "Dark Mode"
    }
    
    // observable - callback when value changes
    var fontSize: Int by Delegates.observable(14) { property, oldValue, newValue ->
        println("${property.name}: $oldValue → $newValue")
    }
}

fun main() {
    val prefs = UserPreferences()
    
    // lazy - first access triggers initialization
    println(prefs.theme) // "Loading theme..." then "Dark Mode"
    println(prefs.theme) // Just "Dark Mode" (cached)
    
    // observable - triggers callback
    prefs.fontSize = 18 // "fontSize: 14 → 18"
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Not keeping delegate reference when you need to call it

```kotlin
interface Settings {
    fun get(key: String): String
}

// Wrong - can't call original implementation!
class LoggingSettings(settings: Settings) : Settings by settings {
    override fun get(key: String): String {
        println("Getting $key")
        return settings.get(key) // ❌ Error: settings not accessible
    }
}
```

### ✅ Correct way

```kotlin
// Right - use 'private val' to keep reference
class LoggingSettings(private val delegate: Settings) : Settings by delegate {
    override fun get(key: String): String {
        println("Getting $key")
        return delegate.get(key) // ✅ Works!
    }
}
```

---

### ❌ Mistake 2: Forgetting `import` for observable/vetoable

```kotlin
class Prefs {
    // Wrong - Delegates not imported!
    var name: String by Delegates.observable("") { _, _, _ -> } // ❌ Error
}
```

### ✅ Correct way

```kotlin
import kotlin.properties.Delegates // Don't forget this!

class Prefs {
    var name: String by Delegates.observable("") { _, old, new ->
        println("Changed: $old → $new")
    }
}
```

---

### ❌ Mistake 3: Using `val` with observable (won't work!)

```kotlin
import kotlin.properties.Delegates

class Prefs {
    // Wrong - observable needs 'var' (it observes CHANGES)
    val fontSize: Int by Delegates.observable(14) { _, _, _ -> } // ❌ Error
}
```

### ✅ Correct way

```kotlin
import kotlin.properties.Delegates

class Prefs {
    // Right - use 'var' for observable
    var fontSize: Int by Delegates.observable(14) { _, old, new ->
        println("$old → $new")
    }
}
```

---

### ❌ Mistake 4: Expecting lazy to reinitialize

```kotlin
class Config {
    val timestamp: Long by lazy {
        System.currentTimeMillis() // Computed once!
    }
}

fun main() {
    val config = Config()
    println(config.timestamp) // e.g., 1234567890
    Thread.sleep(1000)
    println(config.timestamp) // Same value! Not recomputed
}
```

### ✅ Understanding

```kotlin
// lazy is for ONE-TIME initialization, not for changing values
// If you need recalculation, use a regular property or function

class Config {
    // Use function for dynamic values
    fun getTimestamp(): Long = System.currentTimeMillis()
    
    // Use lazy only for expensive one-time setup
    val database: Database by lazy {
        println("Initializing database...")
        Database.connect()
    }
}
```

---

## 🎯 Mini Task

**What we're building:**  
A settings manager that stores values in memory, wrapped with logging functionality using class delegation, plus user preferences that react to changes using property delegates.

**What you'll learn:**
- How to implement interfaces via delegation (`by` keyword)
- How to add behavior (logging) without modifying original class
- How to use `lazy` for deferred initialization
- How to use `observable` to react to property changes
- Why composition/delegation is often better than inheritance

---

## 📚 Quick Recap

- **Class delegation (`by`)** automatically forwards interface methods to another object
- Keep a `private val` reference to the delegate if you need to call it in overridden methods
- **`lazy`** initializes property only on first access - perfect for expensive operations
- **`observable`** calls a callback whenever property changes - great for UI updates
- Use `var` with `observable` (not `val` - can't observe changes to immutable property!)
- Import `kotlin.properties.Delegates` for `observable` and `vetoable`
- Delegation enables the **decorator pattern** - wrap and extend without inheritance

**Next Topic:** Generics (type parameters for reusable, type-safe classes and functions)