# Data Modeling (Classes and Objects)

> Blueprint-based data structures that model real-world entities in your Android app

---

## 🤔 What is it?

**Data classes** are Kotlin's way of creating objects that hold data. They automatically generate useful methods like `equals()`, `hashCode()`, `toString()`, and `copy()` so you don't have to write boilerplate code.

Think of them as smart containers for your app's data - like a user profile, a product, a message, or a bank account. You define what data it holds, and Kotlin handles the rest.

---

## 💡 Why do we need it in Android?

Every Android app works with data. When you build a chat app, you need a `Message` object. Building a shopping app? You need `Product` and `Order` objects. Fetching user profiles from an API? You need a `User` object to store that JSON response.

```kotlin
// Without data class - you'd write ALL this manually 😫
class User(val id: String, val name: String) {
    override fun equals(other: Any?): Boolean { /* 10 lines */ }
    override fun hashCode(): Int { /* 5 lines */ }
    override fun toString(): String { /* 3 lines */ }
    fun copy(id: String = this.id, name: String = this.name) { /* ... */ }
}

// With data class - Kotlin generates everything! 🎉
data class User(val id: String, val name: String)
```

Data classes make your code cleaner and prevent bugs by ensuring consistent behavior across all your model objects.

---

## 📌 Key Concepts

- **Data Class**: A class marked with `data` keyword that auto-generates `equals()`, `hashCode()`, `toString()`, and `copy()`
- **Primary Constructor**: Parameters defined in the class header - these become properties
- **Init Block**: Code that runs when an object is created - perfect for validation
- **Computed Property**: A property that calculates its value dynamically (uses `val` with a getter)
- **Structural Equality (`==`)**: Compares objects by their property values, not memory address
- **Copy Function**: Creates a new instance with some properties changed while keeping others the same

---

## ✍️ Syntax

```kotlin
// Basic data class
data class User(
    val id: String,
    val name: String,
    val age: Int
)

// Data class with default values and validation
data class BankAccount(
    val accountNumber: String,
    val holderName: String,
    val balance: Double = 0.0
) {
    // Init block - runs during object creation
    init {
        require(balance >= 0) { "Balance cannot be negative" }
        require(holderName.isNotBlank()) { "Name required" }
    }
    
    // Computed property - recalculates each time accessed
    val isActive: Boolean
        get() = balance > 0
}

// Usage
fun main() {
    val user = User("123", "John", 25)
    
    // Auto-generated toString()
    println(user) // User(id=123, name=John, age=25)
    
    // Auto-generated copy()
    val olderUser = user.copy(age = 26)
    
    // Auto-generated equals()
    println(user == olderUser) // false (age differs)
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Forgetting `val` or `var` in constructor parameters

```kotlin
// Wrong - these are just constructor params, NOT properties!
data class User(id: String, name: String)

fun main() {
    val user = User("123", "John")
    println(user.name) // ❌ Error: name is not accessible
}
```

### ✅ Correct way

```kotlin
// Right - 'val' makes them properties
data class User(val id: String, val name: String)

fun main() {
    val user = User("123", "John")
    println(user.name) // ✅ Works!
}
```

---

### ❌ Mistake 2: Using `var` when data shouldn't change

```kotlin
// Wrong - mutable properties make data classes unpredictable
data class Product(var id: String, var price: Double)

fun main() {
    val product = Product("P1", 99.0)
    product.id = "P2" // 😱 ID changed! Dangerous in production
}
```

### ✅ Correct way

```kotlin
// Right - use 'val' for immutable data (safer!)
data class Product(val id: String, val price: Double)

fun main() {
    val product = Product("P1", 99.0)
    // To "change" it, create a new copy
    val updated = product.copy(price = 89.0)
}
```

---

### ❌ Mistake 3: Putting validation logic in the wrong place

```kotlin
// Wrong - validation happens too late (after object is created)
data class BankAccount(val balance: Double) {
    fun validate() {
        require(balance >= 0) // Called manually - easy to forget!
    }
}

fun main() {
    val account = BankAccount(-100.0) // ✅ Created successfully (bad!)
    account.validate() // 💥 Crashes here
}
```

### ✅ Correct way

```kotlin
// Right - validation in 'init' block (runs automatically)
data class BankAccount(val balance: Double) {
    init {
        require(balance >= 0) { "Balance cannot be negative" }
    }
}

fun main() {
    val account = BankAccount(-100.0) // 💥 Fails immediately (good!)
}
```

---

## 🎯 Mini Task

**What we're building:**  
A banking account system that validates data on creation, compares accounts correctly, safely creates modified copies, and checks for low balances.

**What you'll learn:**
- How to create data classes with default values
- How to validate data using `init` blocks
- How equality works with data classes (value-based, not reference-based)
- How to use `copy()` to create modified versions of objects
- How to add computed properties for dynamic calculations

---

## 📚 Quick Recap

- **Data classes** auto-generate `equals()`, `hashCode()`, `toString()`, and `copy()` methods
- Use `val` in the constructor to create properties (not just parameters)
- **Init blocks** run during object creation - perfect for validation with `require()`
- **Equality (`==`)** compares property values, not memory addresses
- Use `copy()` to create modified versions while keeping original data immutable
- **Computed properties** recalculate their value each time they're accessed

**Next Topic:** Companion Objects and Object Declarations (for singletons and utility functions)