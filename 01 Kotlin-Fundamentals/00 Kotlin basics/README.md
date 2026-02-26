# Kotlin Fundamentals - Complete Basics

> Master the essential building blocks: variables, null safety, functions, lambdas, and scope functions

---

## 🎯 What You'll Learn

This comprehensive guide covers ALL Kotlin fundamentals you need before diving into Android development. Each concept builds on the previous one, culminating in a mega challenge that tests everything together.

---

## 📑 Table of Contents

- [A. Variables & Types](#a-variables--types)
- [B. Null Safety](#b-null-safety)
- [C. lateinit & lazy](#c-lateinit--lazy)
- [D. Functions](#d-functions)
- [E. Lambdas & Higher-Order Functions](#e-lambdas--higher-order-functions)
- [F. Scope Functions](#f-scope-functions)
- [G. Inline Functions](#g-inline-functions)
- [Mega Build Challenge](#-mega-build-challenge)

---

## A. Variables & Types

### 🤔 What is it?

Variables store data. Kotlin has `val` (immutable - can't change), `var` (mutable - can change), and `const val` (compile-time constant).

### ✍️ Syntax

```kotlin
// val - read-only (assign once)
val name: String = "John"
val age = 25  // Type inferred as Int

// var - mutable (can reassign)
var score = 0
score = 100  // ✅ Allowed

// const val - compile-time constant (top-level or in object)
const val MAX_USERS = 100
const val API_KEY = "abc123"

// Type inference
val city = "Mumbai"        // String inferred
val count = 42             // Int inferred
val price = 99.99          // Double inferred
val isActive = true        // Boolean inferred

// Explicit types
val email: String = "test@example.com"
val userId: Int = 12345
```

### ⚠️ Common Mistakes

```kotlin
// ❌ Trying to change val
val name = "John"
name = "Jane"  // Error: Val cannot be reassigned

// ✅ Use var for mutable data
var name = "John"
name = "Jane"  // Works

// ❌ const val inside function
fun test() {
    const val MAX = 10  // Error: const only at top-level or in object
}

// ✅ Use const val at top-level or in object
const val MAX = 10
object Config {
    const val TIMEOUT = 5000
}
```

---

## B. Null Safety

### 🤔 What is it?

Kotlin forces you to handle `null` explicitly. Use `?` to allow null, `?.` for safe calls, `?:` (Elvis) for defaults, and `!!` (risky!) to assert non-null.

### ✍️ Syntax

```kotlin
// Nullable types - add ?
var name: String? = null
var age: Int? = null

// Safe call (?.) - returns null if receiver is null
val length = name?.length  // null if name is null

// Elvis operator (?:) - provide default value
val displayName = name ?: "Guest"
val userAge = age ?: 0

// let - execute block only if not null
name?.let {
    println("Name is $it")
}

// Safe casting (as?)
val user: Any = "John"
val userName = user as? String  // Returns String or null

// Not-null assertion (!!) - throws if null (use carefully!)
val forcedName = name!!  // 💥 Crashes if name is null

// Chaining
val email = user?.profile?.email ?: "no-email@example.com"
```

### ⚠️ Common Mistakes

```kotlin
// ❌ Using !! everywhere
fun process(user: User?) {
    println(user!!.name)  // 💥 Crashes if user is null
}

// ✅ Use safe calls and Elvis
fun process(user: User?) {
    println(user?.name ?: "Unknown")
}

// ❌ Forgetting ? in type
var email: String = null  // Error: null cannot be assigned

// ✅ Use nullable type
var email: String? = null
```

### 🎯 Quick Reference

```kotlin
val name: String? = getUserName()

// Safe call
name?.uppercase()

// Elvis (default)
name ?: "Guest"

// let (execute if not null)
name?.let { println(it) }

// Null check
if (name != null) {
    println(name.length)  // Smart cast to non-null
}
```

---

## C. lateinit & lazy

### 🤔 What is it?

**`lateinit`** promises to initialize a `var` before use (checked at runtime). **`lazy`** initializes a `val` only when first accessed (great for expensive operations).

### ✍️ Syntax

```kotlin
// lateinit - for var (must initialize before use)
class UserManager {
    lateinit var currentUser: User
    
    fun login(user: User) {
        currentUser = user
    }
    
    fun hasUser(): Boolean = ::currentUser.isInitialized
}

// lazy - for val (initialized on first access)
class Config {
    val database: Database by lazy {
        println("Initializing database...")
        Database.connect()  // Runs only once
    }
}

fun main() {
    val config = Config()
    // Database not initialized yet
    
    val db = config.database  // "Initializing database..." printed
    val db2 = config.database // Already initialized, no print
}
```

### ⚠️ Common Mistakes

```kotlin
// ❌ Using lateinit with nullable
lateinit var name: String?  // Error: lateinit not allowed for nullable

// ✅ lateinit is for non-null vars
lateinit var name: String

// ❌ Using lateinit with val
lateinit val user: User  // Error: lateinit only for var

// ✅ Use lazy for val
val user: User by lazy { User() }

// ❌ Accessing lateinit before initialization
lateinit var name: String
println(name)  // 💥 UninitializedPropertyAccessException

// ✅ Check if initialized
if (::name.isInitialized) {
    println(name)
}
```

---

## D. Functions

### 🤔 What is it?

Functions are reusable blocks of code. Kotlin has regular functions, single-expression functions, default parameters, and named arguments.

### ✍️ Syntax

```kotlin
// Basic function
fun greet(name: String): String {
    return "Hello, $name"
}

// Single-expression function (= syntax)
fun add(a: Int, b: Int): Int = a + b

// Unit return type (like void)
fun printMessage(msg: String) {
    println(msg)
}

// Default parameters
fun createUser(
    name: String,
    age: Int = 18,
    country: String = "India"
): User {
    return User(name, age, country)
}

// Named arguments
val user = createUser(
    name = "John",
    country = "USA"
    // age uses default value
)

// Extension function
fun String.isValidEmail(): Boolean {
    return contains("@") && contains(".")
}

val valid = "test@example.com".isValidEmail()  // true
```

### ⚠️ Common Mistakes

```kotlin
// ❌ Forgetting return type for non-Unit functions
fun calculate(a: Int, b: Int) {
    a + b  // Doesn't return anything!
}

// ✅ Specify return type
fun calculate(a: Int, b: Int): Int = a + b

// ❌ Using positional args in wrong order with defaults
fun createUser(name: String, age: Int = 18, country: String = "India")
createUser("USA", 25)  // Wrong: "USA" assigned to name!

// ✅ Use named arguments
createUser(country = "USA", age = 25, name = "John")
```

---

## E. Lambdas & Higher-Order Functions

### 🤔 What is it?

**Lambdas** are anonymous functions. **Higher-order functions** take functions as parameters or return functions.

### ✍️ Syntax

```kotlin
// Lambda syntax
val greet: (String) -> String = { name -> "Hello, $name" }

// Single parameter - use 'it'
val double: (Int) -> Int = { it * 2 }

// Multiple parameters
val add: (Int, Int) -> Int = { a, b -> a + b }

// Higher-order function
fun processNumbers(
    numbers: List<Int>,
    operation: (Int) -> Int
): List<Int> {
    return numbers.map(operation)
}

// Usage
val result = processNumbers(listOf(1, 2, 3)) { it * 2 }
// [2, 4, 6]

// Trailing lambda syntax
val filtered = listOf(1, 2, 3, 4, 5).filter { it > 2 }

// Function reference
fun isEven(num: Int): Boolean = num % 2 == 0
val evens = listOf(1, 2, 3, 4).filter(::isEven)

// Multiple lambdas
fun authenticate(
    username: String,
    onSuccess: (User) -> Unit,
    onError: (String) -> Unit
) {
    if (username.isNotBlank()) {
        onSuccess(User(username))
    } else {
        onError("Invalid username")
    }
}

authenticate(
    username = "john",
    onSuccess = { user -> println("Welcome ${user.name}") },
    onError = { error -> println("Error: $error") }
)
```

### ⚠️ Common Mistakes

```kotlin
// ❌ Confusing lambda syntax
val add = { a: Int, b: Int -> a + b }  // Missing type declaration

// ✅ Declare full type
val add: (Int, Int) -> Int = { a, b -> a + b }

// ❌ Using parentheses for trailing lambda
list.filter({ it > 2 })  // Valid but not idiomatic

// ✅ Use trailing lambda
list.filter { it > 2 }
```

---

## F. Scope Functions

### 🤔 What is it?

Scope functions (`let`, `run`, `with`, `apply`, `also`) execute a block of code in the context of an object.

### ✍️ Syntax

```kotlin
data class User(var name: String, var age: Int)

// let - use 'it', returns lambda result
val length = "Hello".let {
    println(it)
    it.length  // Returns 5
}

user?.let {
    println("User name: ${it.name}")
}

// apply - use 'this', returns receiver
val user = User("", 0).apply {
    name = "John"
    age = 25
}  // Returns configured User

// also - use 'it', returns receiver
val user = User("John", 25).also {
    println("Created user: ${it.name}")
}  // Returns same User

// run - use 'this', returns lambda result
val isAdult = user.run {
    age >= 18  // Returns Boolean
}

// with - not an extension, use 'this', returns lambda result
val message = with(user) {
    "Name: $name, Age: $age"
}
```

### 📊 Quick Comparison

| Function | Reference | Returns | Use Case |
|----------|-----------|---------|----------|
| `let` | `it` | Lambda result | Null checks, transformations |
| `apply` | `this` | Receiver | Object configuration |
| `also` | `it` | Receiver | Side effects (logging) |
| `run` | `this` | Lambda result | Compute from object |
| `with` | `this` | Lambda result | Multiple calls on object |

### ⚠️ Common Mistakes

```kotlin
// ❌ Using apply when you need let
val name = user.apply {
    name.uppercase()  // apply returns User, not String!
}

// ✅ Use let for transformation
val name = user.name.let { it.uppercase() }

// ❌ Confusing 'it' and 'this'
user.let {
    this.name  // Error: 'this' is outer class
}

// ✅ Use 'it' in let
user.let {
    it.name
}
```

---

## G. Inline Functions

### 🤔 What is it?

**`inline`** copies function code to call site (avoids lambda object creation). **`reified`** lets you access generic types at runtime.

### ✍️ Syntax

```kotlin
// inline - avoids lambda overhead
inline fun measureTime(block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    println("Time: ${end - start}ms")
}

// inline + reified - type checking at runtime
inline fun <reified T> List<Any>.filterByType(): List<T> {
    return filter { it is T }.map { it as T }
}

// Usage
val mixed = listOf(1, "hello", 2, "world", 3)
val strings = mixed.filterByType<String>()  // ["hello", "world"]
val ints = mixed.filterByType<Int>()        // [1, 2, 3]

// noinline - prevent inlining for specific lambda
inline fun process(
    inline action: () -> Unit,
    noinline logger: () -> Unit
) {
    action()
    saveLogger(logger)  // Can store noinline lambda
}
```

### ⚠️ Common Mistakes

```kotlin
// ❌ Using reified without inline
fun <reified T> check() { }  // Error: reified requires inline

// ✅ Add inline
inline fun <reified T> check() { }

// ❌ Making large functions inline
inline fun processHugeData() {
    // 100 lines of code
}  // Code duplicated everywhere it's called!

// ✅ Use inline only for small, frequently-called functions
inline fun log(msg: String) = println(msg)
```

---

## 🏗️ MEGA BUILD CHALLENGE

**What we're building:** A complete user management system that uses EVERY fundamental concept you've learned.

### Requirements

```kotlin
// ===================
// PART A: Constants & Config
// ===================
const val MAX_NAME_LENGTH = 50
const val MIN_AGE = 13
const val MAX_AGE = 120
const val DEFAULT_COUNTRY = "India"

// ===================
// PART B: Data Class with Nullables
// ===================
data class User(
    val id: String,
    val name: String,
    val email: String?,
    val age: Int,
    val country: String = DEFAULT_COUNTRY
)

// ===================
// PART C: Extension Functions
// ===================
fun String.isValidEmail(): Boolean = contains("@") && contains(".")

fun Int.isValidAge(): Boolean = this in MIN_AGE..MAX_AGE

fun User.getDisplayName(): String = 
    email?.let { "$name ($it)" } ?: name

// ===================
// PART D: Nullable Handling
// ===================
fun processEmail(email: String?): String =
    email?.uppercase() ?: "NO EMAIL"

fun sendWelcome(user: User) {
    user.email?.let {
        println("Sending email to $it")
    } ?: println("No email to send")
}

// ===================
// PART E: lateinit & lazy
// ===================
class Analytics {
    init {
        println("Analytics initialized")
    }
}

class UserManager {
    lateinit var currentUser: User
    
    val analytics: Analytics by lazy {
        println("Creating analytics...")
        Analytics()
    }
    
    fun hasCurrentUser(): Boolean = ::currentUser.isInitialized
}

// ===================
// PART F: Higher-Order Function
// ===================
fun processUsers(
    users: List<User>,
    filter: (User) -> Boolean,
    transform: (User) -> String
): List<String> {
    return users.filter(filter).map(transform)
}

// ===================
// PART G: Scope Functions
// ===================
fun createUser(name: String, email: String?, age: Int): User? {
    return name.takeIf { it.length <= MAX_NAME_LENGTH }
        ?.let { validName ->
            User(
                id = System.currentTimeMillis().toString(),
                name = validName,
                email = email,
                age = age
            )
        }
        ?.apply {
            println("Configuring user $name")
        }
        ?.also {
            println("User created: ${it.id}")
        }
}

// ===================
// PART H: Inline + Reified
// ===================
inline fun <reified T> List<Any>.filterByType(): List<T> {
    return filterIsInstance<T>()
}

// ===================
// PART I: Test Everything in main()
// ===================
fun main() {
    println("🎯 MEGA CHALLENGE - User Management System\n")
    
    // Test Part B & C - Create users
    val user1 = User("1", "Alice", "alice@test.com", 25)
    val user2 = User("2", "Bob", null, 30)
    val user3 = User("3", "Charlie", "charlie@example.com", 17)
    
    // Test Part C - Extension functions
    println("=== Extension Functions ===")
    println("Email valid: ${"test@example.com".isValidEmail()}")
    println("Age valid: ${25.isValidAge()}")
    println("Display: ${user1.getDisplayName()}")
    println("Display (no email): ${user2.getDisplayName()}\n")
    
    // Test Part D - Nullable handling
    println("=== Nullable Handling ===")
    println(processEmail(user1.email))
    println(processEmail(user2.email))
    sendWelcome(user1)
    sendWelcome(user2)
    println()
    
    // Test Part E - lateinit & lazy
    println("=== lateinit & lazy ===")
    val manager = UserManager()
    println("Has user: ${manager.hasCurrentUser()}")
    manager.currentUser = user1
    println("Has user: ${manager.hasCurrentUser()}")
    println("Accessing analytics...")
    manager.analytics  // Triggers lazy init
    manager.analytics  // Already initialized
    println()
    
    // Test Part F - Higher-order functions
    println("=== Higher-Order Functions ===")
    val users = listOf(user1, user2, user3)
    val result = processUsers(
        users = users,
        filter = { it.age >= 18 },
        transform = { "${it.name} (${it.age})" }
    )
    println("Adults: $result\n")
    
    // Test Part G - Scope functions
    println("=== Scope Functions ===")
    val newUser = createUser("Diana", "diana@test.com", 22)
    val invalidUser = createUser("A".repeat(60), "long@test.com", 20)
    println("Valid user: ${newUser?.name}")
    println("Invalid user: ${invalidUser?.name}\n")
    
    // Test Part H - Inline + reified
    println("=== Inline + Reified ===")
    val mixed: List<Any> = listOf(1, "hello", 2, user1, "world", 3, user2)
    val strings = mixed.filterByType<String>()
    val ints = mixed.filterByType<Int>()
    val usersFiltered = mixed.filterByType<User>()
    
    println("Strings: $strings")
    println("Ints: $ints")
    println("Users: ${usersFiltered.map { it.name }}\n")
    
    // Test chaining everything
    println("=== Final Chain ===")
    val finalResult = users
        .filter { it.age.isValidAge() }
        .also { println("Valid age users: ${it.size}") }
        .map { it.getDisplayName() }
        .let { names ->
            "Users: ${names.joinToString()}"
        }
    
    println(finalResult)
}
```

---

## 📚 Complete Recap

### Variables & Types
- `val` = immutable, `var` = mutable, `const val` = compile-time constant
- Type inference reduces boilerplate

### Null Safety
- `String?` for nullable, `?.` for safe calls, `?:` for defaults
- `?.let { }` executes only if not null

### lateinit & lazy
- `lateinit var` for late initialization (checked at runtime)
- `lazy val` for expensive one-time initialization

### Functions
- Single-expression functions with `=`
- Default parameters and named arguments
- Extension functions add methods to existing classes

### Lambdas & Higher-Order
- `{ param -> body }` syntax, `it` for single parameter
- Functions can take/return other functions
- Trailing lambda syntax for cleaner code

### Scope Functions
- `let` (it, result), `apply` (this, receiver), `also` (it, receiver)
- `run` (this, result), `with` (this, result)

### Inline Functions
- `inline` avoids lambda overhead
- `reified` enables runtime type checking

---

**Next Topic:** Object-Oriented Programming (Classes, Inheritance, Interfaces, Sealed Classes)