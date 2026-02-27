# Singletons & Companion Objects (Beginner Friendly)

> One instance for the whole app, plus factory methods that belong to the class itself

---

## 🤔 What is it?

**Singleton (`object`)** creates exactly ONE instance that exists for the entire app lifecycle. **Companion object** is a singleton attached to a class - perfect for factory methods and constants that belong to the class but don't need an instance.

Think of a singleton like the Sun - there's only one, everyone shares it. Companion objects are like a class's personal assistant - they help create instances and hold shared info.

---

## 💡 Why do we need it in Android?

Android apps need shared managers: one database instance, one network client, one analytics tracker. Creating multiple instances wastes memory and causes bugs. Singletons solve this.

```kotlin
// Without singleton - chaos! 😫
val db1 = Database() // First instance
val db2 = Database() // Second instance - inconsistent data!
db1.save(user)
db2.getUsers() // Doesn't see the saved user!

// With singleton - single source of truth! 🎉
object Database {
    private val users = mutableListOf<User>()
    fun save(user: User) = users.add(user)
    fun getUsers() = users.toList()
}

Database.save(user)
Database.getUsers() // Always consistent!
```

Companion objects are everywhere in Android: `Fragment.newInstance()`, `Intent.createChooser()`, `MediaType.parse()`.

---

## 📌 Key Concepts

- **`object` Declaration**: Creates a singleton - ONE instance, created lazily on first access
- **Companion Object**: A singleton inside a class, accessed via class name
- **Factory Method**: A function that creates instances (like constructors but with custom logic)
- **Constants**: `const val` for compile-time constants, `val` for runtime constants
- **Lazy Initialization**: Singletons are created only when first accessed
- **Thread Safety**: Kotlin `object` declarations are thread-safe by default

---

## ✍️ Syntax

```kotlin
// ============================================
// SINGLETON - object declaration
// ============================================

object BankManager {
    // Private state
    private val accounts = mutableListOf<Account>()
    
    // Public functions
    fun addAccount(account: Account) {
        accounts.add(account)
    }
    
    fun getAccountCount(): Int = accounts.size
    
    fun getTotalBalance(): Double = accounts.sumOf { it.balance }
    
    // Can have init block
    init {
        println("BankManager initialized!")
    }
}

// Usage - no instantiation needed!
fun main() {
    BankManager.addAccount(account1)
    BankManager.addAccount(account2)
    println(BankManager.getAccountCount()) // 2
}

// ============================================
// COMPANION OBJECT - belongs to a class
// ============================================

data class Account(
    val accountNumber: String,
    val holderName: String,
    val balance: Double
) {
    // Companion object - accessed via Account.xxx
    companion object {
        // Compile-time constant
        const val MIN_BALANCE = 500.0
        
        // Runtime constant
        val DEFAULT_TYPE = "SAVINGS"
        
        // Factory method
        fun createEmpty(accountNumber: String, holderName: String): Account {
            return Account(
                accountNumber = accountNumber,
                holderName = holderName,
                balance = 0.0
            )
        }
        
        // Another factory method
        fun createWithMinBalance(accountNumber: String, holderName: String): Account {
            return Account(
                accountNumber = accountNumber,
                holderName = holderName,
                balance = MIN_BALANCE
            )
        }
    }
}

// Usage
fun main() {
    // Access constant via class name
    println(Account.MIN_BALANCE) // 500.0
    
    // Use factory method
    val account = Account.createEmpty("1234567890", "John")
    
    // Regular constructor still works
    val account2 = Account("0987654321", "Jane", 1000.0)
}

// ============================================
// NAMED COMPANION OBJECT
// ============================================

class User(val name: String) {
    companion object Factory {
        fun create(name: String): User = User(name)
    }
}

// Both work
val user1 = User.create("John")
val user2 = User.Factory.create("Jane")

// ============================================
// COMPANION OBJECT IMPLEMENTING INTERFACE
// ============================================

interface JsonParser<T> {
    fun fromJson(json: String): T
}

data class User(val id: String, val name: String) {
    companion object : JsonParser<User> {
        override fun fromJson(json: String): User {
            // Parse JSON and return User
            return User("123", "John")
        }
    }
}

// Usage
val user = User.fromJson("""{"id": "123", "name": "John"}""")
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Trying to instantiate an `object`

```kotlin
object Database {
    fun connect() { }
}

fun main() {
    val db = Database() // ❌ Error: Cannot create instance of object
}
```

### ✅ Correct way

```kotlin
object Database {
    fun connect() { }
}

fun main() {
    Database.connect() // ✅ Access directly via name
}
```

---

### ❌ Mistake 2: Using `const` for non-primitive types

```kotlin
data class Account(val id: String) {
    companion object {
        const val DEFAULT_ACCOUNT = Account("000") // ❌ Error!
        // const only works with primitives and String
    }
}
```

### ✅ Correct way

```kotlin
data class Account(val id: String) {
    companion object {
        const val DEFAULT_ID = "000" // ✅ String works
        const val MIN_BALANCE = 500.0 // ✅ Double works
        
        val DEFAULT_ACCOUNT = Account("000") // ✅ Use val for objects
    }
}
```

---

### ❌ Mistake 3: Forgetting companion object when accessing from class

```kotlin
data class User(val name: String) {
    val DEFAULT_NAME = "Unknown" // Instance property!
}

fun main() {
    println(User.DEFAULT_NAME) // ❌ Error: can't access without instance
}
```

### ✅ Correct way

```kotlin
data class User(val name: String) {
    companion object {
        const val DEFAULT_NAME = "Unknown" // Belongs to companion
    }
}

fun main() {
    println(User.DEFAULT_NAME) // ✅ Works!
}
```

---

### ❌ Mistake 4: Creating multiple singletons when one is needed

```kotlin
// Wrong - manually implementing singleton pattern
class DatabaseManager private constructor() {
    companion object {
        private var instance: DatabaseManager? = null
        
        fun getInstance(): DatabaseManager {
            if (instance == null) {
                instance = DatabaseManager()
            }
            return instance!!
        }
    }
}
```

### ✅ Correct way

```kotlin
// Right - just use object declaration!
object DatabaseManager {
    fun connect() { }
    fun query(sql: String) { }
}

// Kotlin handles everything: lazy init, thread safety, single instance
```

---

### ❌ Mistake 5: Mutable state in singleton without caution

```kotlin
object UserCache {
    var currentUser: User? = null // ⚠️ Mutable global state
}

// Problem: Anyone can modify from anywhere
fun someFunction() {
    UserCache.currentUser = null // Unexpected side effect!
}
```

### ✅ Correct way

```kotlin
object UserCache {
    private var _currentUser: User? = null
    
    // Read-only access
    val currentUser: User?
        get() = _currentUser
    
    // Controlled modification
    fun setUser(user: User) {
        _currentUser = user
    }
    
    fun clear() {
        _currentUser = null
    }
}
```

---

## 📊 Quick Comparison

| Feature | `object` (Singleton) | `companion object` |
|---------|---------------------|-------------------|
| Instance | ONE for entire app | ONE per class |
| Access | `ObjectName.method()` | `ClassName.method()` |
| Purpose | Shared managers/utilities | Factory methods, constants |
| Can hold state | ✅ Yes | ✅ Yes |
| Can implement interfaces | ✅ Yes | ✅ Yes |

---

## 🧠 When to Use What?

```kotlin
// Use OBJECT for:
// - Managers (DatabaseManager, NetworkManager)
// - Utilities (StringUtils, DateUtils)
// - Stateless services (Logger, Analytics)

object Logger {
    fun log(message: String) = println("[LOG] $message")
}

object NetworkClient {
    private val client = OkHttpClient()
    fun get(url: String): Response = client.newCall(Request(url)).execute()
}

// Use COMPANION OBJECT for:
// - Factory methods (create instances with custom logic)
// - Constants related to the class
// - Static-like behavior

data class User(val id: String, val name: String, val email: String) {
    companion object {
        const val MAX_NAME_LENGTH = 50
        
        fun fromEmail(email: String): User {
            val name = email.substringBefore("@")
            val id = UUID.randomUUID().toString()
            return User(id, name, email)
        }
    }
}
```

---

## 🎯 Mini Task

**What we're building:**  
A simple banking system with a data class for accounts (including factory methods and constants in companion object) and a singleton BankManager to manage all accounts.

**What you'll learn:**
- How to create singletons with `object`
- How to add companion objects with constants and factory methods
- How to use `const val` vs `val` for constants
- How factory methods simplify object creation
- Real patterns used in Android for managers and utilities

---

## 📚 Quick Recap

- **`object`** creates exactly ONE instance for the whole app (singleton)
- **`companion object`** is a singleton attached to a class - accessed via class name
- Use **`const val`** for primitives/strings, **`val`** for complex objects
- **Factory methods** in companion objects provide controlled instantiation
- Kotlin `object` is thread-safe and lazy-initialized automatically
- Don't manually implement singleton pattern - just use `object`
- Common Android uses: managers, caches, utilities, factories
- Singletons live for entire app lifecycle - be careful with mutable state

**Next Topic:** Extension Functions (add methods to existing classes without inheritance)