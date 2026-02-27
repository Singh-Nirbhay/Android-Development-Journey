# Sealed Classes (CRITICAL FOR ANDROID)

> Type-safe state management that makes impossible states impossible to represent

---

## 🤔 What is it?

**Sealed classes** are restricted class hierarchies where all subclasses are known at compile time. Think of them as enums on steroids - they can hold data, have multiple instances, and guarantee exhaustive `when` expressions.

They're like a multiple-choice question where you know ALL possible answers upfront. The compiler can verify you've handled every case, eliminating entire categories of runtime crashes.

---

## 💡 Why do we need it in Android?

Every Android screen has states: loading, success, error, empty. Without sealed classes, you'd use booleans or strings, leading to impossible states like "loading AND error simultaneously" or forgetting to handle a case.

```kotlin
// Without sealed classes - bug-prone! 😫
data class UiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val data: User? = null,
    val errorMessage: String? = null
)
// What if isLoading=true AND isError=true? 🤔
// What if data is null but isError is false? 🤔

// With sealed classes - only valid states possible! 🎉
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: User) : UiState()
    data class Error(val message: String) : UiState()
}
// Impossible to have conflicting states! ✅
```

**This is THE Android pattern** for ViewModels, API responses, screen states, navigation, and more. Google's official Android samples use this everywhere.

---

## 📌 Key Concepts

- **Sealed Class**: A restricted hierarchy where all subclasses must be defined in the same file/package
- **Exhaustive `when`**: The compiler forces you to handle ALL possible subclasses (no `else` needed)
- **Object Subclass**: A singleton state with no data (like `Loading`, `Idle`)
- **Data Class Subclass**: A state that carries data (like `Success(user)`, `Error(message)`)
- **Type Safety**: Each state knows exactly what data it holds - no nulls, no guessing
- **Single Source of Truth**: Only ONE state active at a time - no conflicting booleans

---

## ✍️ Syntax

```kotlin
// Define sealed class hierarchy
sealed class LoginState {
    // Object - singleton state (no data needed)
    object Idle : LoginState()
    object Loading : LoginState()
    
    // Data class - state with data
    data class Success(val userId: String, val userName: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

// Exhaustive when (compiler enforces ALL cases)
fun render(state: LoginState) {
    when (state) {
        LoginState.Idle -> println("Enter credentials")
        LoginState.Loading -> println("Loading...")
        is LoginState.Success -> println("Welcome ${state.userName}!")
        is LoginState.Error -> println("Error: ${state.message}")
        // No 'else' needed - compiler knows these are ALL cases!
    }
}

// Usage
fun main() {
    var state: LoginState = LoginState.Idle
    render(state) // "Enter credentials"
    
    state = LoginState.Loading
    render(state) // "Loading..."
    
    state = LoginState.Success("123", "John")
    render(state) // "Welcome John!"
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Forgetting `is` keyword for data class branches

```kotlin
sealed class LoginState {
    object Loading : LoginState()
    data class Success(val user: String) : LoginState()
}

fun render(state: LoginState) {
    when (state) {
        LoginState.Loading -> println("Loading")
        LoginState.Success -> println(state.user) // ❌ Error: Success is a class!
    }
}
```

### ✅ Correct way

```kotlin
fun render(state: LoginState) {
    when (state) {
        LoginState.Loading -> println("Loading")
        is LoginState.Success -> println(state.user) // ✅ 'is' for type checking
    }
}
```

---

### ❌ Mistake 2: Using `class` instead of `object` for stateless subclasses

```kotlin
sealed class LoginState {
    // Wrong - creates new instance each time (wastes memory)
    class Loading : LoginState()
}

fun main() {
    val state1 = LoginState.Loading() // New instance
    val state2 = LoginState.Loading() // Another new instance
    println(state1 === state2) // false (different objects)
}
```

### ✅ Correct way

```kotlin
sealed class LoginState {
    // Right - singleton, reused everywhere
    object Loading : LoginState()
}

fun main() {
    val state1 = LoginState.Loading // Same instance
    val state2 = LoginState.Loading // Same instance
    println(state1 === state2) // true (same object)
}
```

---

### ❌ Mistake 3: Adding `else` branch (defeats the purpose!)

```kotlin
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: String) : LoginState()
}

fun render(state: LoginState) {
    when (state) {
        LoginState.Idle -> println("Idle")
        else -> println("Other") // ❌ Hides bugs! What if you add Error state?
    }
}
```

### ✅ Correct way

```kotlin
fun render(state: LoginState) {
    // Right - compiler forces you to handle ALL cases
    when (state) {
        LoginState.Idle -> println("Idle")
        LoginState.Loading -> println("Loading")
        is LoginState.Success -> println("Success: ${state.user}")
        // If you add 'Error' state, code won't compile until you handle it! ✅
    }
}
```

---

### ❌ Mistake 4: Making sealed class a data class

```kotlin
// Wrong - sealed classes shouldn't be data classes
sealed data class LoginState { // ❌ Syntax error
    object Loading : LoginState()
}
```

### ✅ Correct way

```kotlin
// Right - sealed class itself is not data class
sealed class LoginState {
    object Loading : LoginState()
    data class Success(val user: String) : LoginState() // Subclasses can be
}
```

---

## 🎯 Mini Task

**What we're building:**  
A production-ready login screen state manager that handles user input, loading states, success, and errors - exactly how real Android apps work with ViewModels and Compose/XML.

**What you'll learn:**
- How to model screen states with sealed classes (Idle, Loading, Success, Error)
- How to model user events with sealed classes (clicks, text changes)
- How to use exhaustive `when` for compile-time safety
- How ViewModels manage state in real Android apps
- Why sealed classes prevent impossible states

---

## 📚 Quick Recap

- **Sealed classes** restrict hierarchies to a known set of subclasses (all in same file)
- Use `object` for singleton states (Loading, Idle) and `data class` for states with data (Success, Error)
- **Exhaustive `when`** forces you to handle ALL cases - compiler catches missing branches
- Use `is` keyword for data class branches to access their properties
- **Never add `else`** to sealed class `when` - you lose compile-time safety
- This is **THE** Android pattern for ViewModels, API responses, navigation, and UI states
- Makes impossible states impossible (no "loading AND error AND success" chaos)

**Next Topic:** Extension Functions (add methods to existing classes without inheritance)