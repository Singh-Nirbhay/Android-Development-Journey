# Error Handling

> Gracefully manage failures with Result, try-catch, and type-safe error states

---

## 🤔 What is it?

**Error handling** is how you manage things that can go wrong - network failures, invalid input, missing data. Kotlin provides `Result<T>`, `runCatching`, try-catch blocks, and sealed classes to handle errors safely without crashing your app.

Think of it as a safety net: instead of your app crashing when something fails, you catch the error, understand what went wrong, and show users a helpful message.

---

## 💡 Why do we need it in Android?

Android apps are full of things that can fail: API calls timeout, users enter invalid data, databases get corrupted, storage is full. Without proper error handling, your app crashes. With it, you show "No internet" instead of a blank screen.

```kotlin
// Without error handling 😫
fun login(email: String, password: String) {
    val user = api.login(email, password) // 💥 Crashes if network fails
    showWelcome(user)
}

// With error handling 🎉
suspend fun login(email: String, password: String): Result<User> {
    return runCatching {
        api.login(email, password)
    }.onFailure { error ->
        when (error) {
            is IOException -> showError("No internet")
            is HttpException -> showError("Server error")
            else -> showError("Login failed")
        }
    }
}
```

This pattern is **everywhere** in modern Android: ViewModels, Repositories, Use Cases - all use `Result<T>` and sealed class states.

---

## 📌 Key Concepts

- **`Result<T>`**: A wrapper that holds either success (`Success(value)`) or failure (`Failure(exception)`)
- **`runCatching { }`**: Executes code and wraps result in `Result<T>` (catches exceptions automatically)
- **`fold(onSuccess, onFailure)`**: Handle both success and failure cases
- **`onSuccess { }` / `onFailure { }`**: React to specific outcome
- **`getOrNull()`**: Extract value or return null if failed
- **`getOrElse { }`**: Extract value or provide default if failed
- **Try-Catch**: Traditional exception handling (use for specific control flow)
- **Sealed Class States**: Type-safe UI states (Idle, Loading, Success, Error)

---

## ✍️ Syntax

```kotlin
// ============================================
// RESULT TYPE - Modern Kotlin approach
// ============================================

// Function returning Result
fun divide(a: Int, b: Int): Result<Int> = runCatching {
    if (b == 0) throw ArithmeticException("Cannot divide by zero")
    a / b
}

// Using Result
fun main() {
    val result = divide(10, 2)
    
    // Method 1: fold (handle both cases)
    result.fold(
        onSuccess = { value -> println("Result: $value") },
        onFailure = { error -> println("Error: ${error.message}") }
    )
    
    // Method 2: onSuccess / onFailure (chainable)
    result
        .onSuccess { println("Success: $it") }
        .onFailure { println("Failed: ${it.message}") }
    
    // Method 3: getOrNull
    val value: Int? = result.getOrNull()
    
    // Method 4: getOrElse
    val safeValue = result.getOrElse { 0 }
    
    // Method 5: getOrThrow (rethrows exception)
    try {
        val unsafeValue = result.getOrThrow()
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
}

// ============================================
// runCatching - Automatic exception handling
// ============================================

suspend fun fetchUser(id: String): Result<User> = runCatching {
    // Any exception thrown here is caught
    val response = api.getUser(id)
    if (!response.isSuccessful) {
        throw HttpException(response.code())
    }
    response.body() ?: throw NullPointerException("User not found")
}

// ============================================
// TRY-CATCH - Traditional approach
// ============================================

fun parseAge(input: String): Int {
    return try {
        input.toInt()
    } catch (e: NumberFormatException) {
        println("Invalid number: $input")
        0  // Default value
    } finally {
        println("Parsing completed")
    }
}

// ============================================
// SEALED CLASS STATES - Type-safe UI states
// ============================================

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val user: User) : UiState()
    data class Error(val message: String) : UiState()
}

// ViewModel using states
class LoginViewModel {
    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()
    
    suspend fun login(email: String, password: String) {
        _state.value = UiState.Loading
        
        repository.login(email, password).fold(
            onSuccess = { user ->
                _state.value = UiState.Success(user)
            },
            onFailure = { error ->
                _state.value = UiState.Error(
                    when (error) {
                        is IllegalArgumentException -> error.message ?: "Invalid input"
                        is IOException -> "No internet connection"
                        is HttpException -> "Server error (${error.code()})"
                        else -> "Something went wrong"
                    }
                )
            }
        )
    }
}

// ============================================
// CUSTOM EXCEPTIONS
// ============================================

class InvalidCredentialsException(message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)

fun authenticate(email: String, password: String): Result<User> = runCatching {
    when {
        email.isBlank() -> throw IllegalArgumentException("Email required")
        password.length < 6 -> throw IllegalArgumentException("Password too short")
        email == "fail@test.com" -> throw InvalidCredentialsException("Invalid credentials")
        else -> User("123", "John Doe", email)
    }
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Not handling Result at all

```kotlin
fun getUser(id: String): Result<User> = runCatching {
    api.fetchUser(id)
}

fun main() {
    val result = getUser("123")
    println(result.name) // ❌ Error: Result doesn't have 'name'
}
```

### ✅ Correct way

```kotlin
fun main() {
    val result = getUser("123")
    
    // Extract value safely
    result.onSuccess { user ->
        println(user.name) // ✅ Works
    }
    
    // Or use getOrNull
    val user = result.getOrNull()
    println(user?.name)
}
```

---

### ❌ Mistake 2: Using `getOrThrow()` without try-catch

```kotlin
fun processUser(id: String) {
    val result = getUser(id)
    val user = result.getOrThrow() // 💥 Crashes if failed!
    println(user.name)
}
```

### ✅ Correct way

```kotlin
fun processUser(id: String) {
    val result = getUser(id)
    
    // Use fold instead
    result.fold(
        onSuccess = { user -> println(user.name) },
        onFailure = { error -> println("Failed: ${error.message}") }
    )
    
    // Or getOrNull
    val user = result.getOrNull()
    if (user != null) {
        println(user.name)
    }
}
```

---

### ❌ Mistake 3: Catching generic Exception without specificity

```kotlin
try {
    val user = api.fetchUser(id)
} catch (e: Exception) {
    // Wrong - can't tell what went wrong!
    showError("Error occurred")
}
```

### ✅ Correct way

```kotlin
try {
    val user = api.fetchUser(id)
} catch (e: IOException) {
    showError("No internet connection")
} catch (e: HttpException) {
    showError("Server error (${e.code()})")
} catch (e: Exception) {
    showError(e.message ?: "Unknown error")
}

// Or use Result with when
repository.getUser(id).onFailure { error ->
    val message = when (error) {
        is IOException -> "No internet"
        is HttpException -> "Server error"
        else -> "Something went wrong"
    }
    showError(message)
}
```

---

### ❌ Mistake 4: Not updating state on error

```kotlin
class ViewModel {
    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    
    suspend fun loadData() {
        _state.value = UiState.Loading
        
        repository.getData()
            .onSuccess { data ->
                _state.value = UiState.Success(data)
            }
        // Wrong - state stays Loading forever if it fails!
    }
}
```

### ✅ Correct way

```kotlin
class ViewModel {
    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    
    suspend fun loadData() {
        _state.value = UiState.Loading
        
        repository.getData().fold(
            onSuccess = { data ->
                _state.value = UiState.Success(data)
            },
            onFailure = { error ->
                _state.value = UiState.Error(error.message ?: "Failed")
            }
        )
    }
}
```

---

### ❌ Mistake 5: Exposing implementation details in error messages

```kotlin
fun login(email: String): Result<User> = runCatching {
    val user = database.query("SELECT * FROM users WHERE email = '$email'")
    // Wrong - SQL query exposed to user!
    user ?: throw Exception("No user found for query: SELECT * FROM...")
}
```

### ✅ Correct way

```kotlin
fun login(email: String): Result<User> = runCatching {
    val user = database.query("SELECT * FROM users WHERE email = '$email'")
    user ?: throw IllegalArgumentException("Invalid email")
}.onFailure { error ->
    // Log technical details internally
    Logger.error("Database query failed", error)
}
```

---

## 📊 Error Handling Patterns

| Pattern | Use When | Example |
|---------|----------|---------|
| `Result<T>` | Repository/API calls | `fun getUser(): Result<User>` |
| `runCatching` | Wrapping risky code | `runCatching { api.call() }` |
| `try-catch` | Specific control flow | `try { parse() } catch (e: NFE)` |
| Sealed States | ViewModel UI states | `Success`, `Error`, `Loading` |
| Custom Exceptions | Domain errors | `InvalidCredentialsException` |

---

## 🧠 Complete Flow Example

```kotlin
// Repository Layer - Returns Result
class AuthRepository {
    suspend fun login(email: String, password: String): Result<User> = 
        runCatching {
            // Validation
            require(email.isNotBlank()) { "Email required" }
            require(password.length >= 6) { "Password too short" }
            
            // API call (can throw IOException, HttpException)
            val response = api.login(email, password)
            
            // Parse response
            response.body() ?: throw Exception("Invalid credentials")
        }
}

// ViewModel Layer - Manages UI state
class LoginViewModel(private val repository: AuthRepository) {
    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            
            repository.login(email, password).fold(
                onSuccess = { user ->
                    _state.value = UiState.Success(user)
                },
                onFailure = { error ->
                    _state.value = UiState.Error(
                        when (error) {
                            is IllegalArgumentException -> error.message!!
                            is IOException -> "Check your internet"
                            is HttpException -> "Server error"
                            else -> "Login failed"
                        }
                    )
                }
            )
        }
    }
}

// UI Layer - Renders state
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val state by viewModel.state.collectAsState()
    
    when (val currentState = state) {
        UiState.Idle -> LoginForm()
        UiState.Loading -> CircularProgressIndicator()
        is UiState.Success -> WelcomeScreen(currentState.user)
        is UiState.Error -> ErrorMessage(currentState.message)
    }
}
```

---

## 🎯 Mini Task

**What we're building:**  
A complete login system with validation, error handling, and user-friendly error messages - exactly how production Android apps work with Repository → ViewModel → UI architecture.

**What you'll learn:**
- How to use `Result<T>` for safe API calls
- How to validate input and throw meaningful exceptions
- How to map technical errors to user-friendly messages
- How to manage UI states with sealed classes
- Complete error handling flow from Repository to UI
- Production patterns used in real Android apps

---

## 📚 Quick Recap

- **`Result<T>`** wraps success or failure - safer than throwing exceptions
- **`runCatching { }`** automatically catches exceptions and returns `Result`
- **`fold(onSuccess, onFailure)`** handles both outcomes explicitly
- Use **`getOrNull()`** for safe extraction, avoid `getOrThrow()` without try-catch
- Catch **specific exceptions** (IOException, HttpException) not generic Exception
- Map technical errors to **user-friendly messages** (no stack traces in UI!)
- Use **sealed class states** (Idle, Loading, Success, Error) for type-safe UI
- Always update state on **both success AND failure**
- Repository returns `Result<T>`, ViewModel manages states, UI renders states

**Next Topic:** Coroutines Basics (asynchronous programming with suspend functions and launch/async)