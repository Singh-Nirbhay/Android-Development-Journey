# Basic Generics

> Write once, use with any type - type-safe reusable code without sacrificing safety

---

## 🤔 What is it?

**Generics** let you write classes and functions that work with any type while keeping compile-time type safety. Instead of writing `IntCache`, `StringCache`, `UserCache` separately, you write one `Cache<T>` that works with everything.

Think of generics like a shipping box - the box doesn't care if it contains books, phones, or toys. It just holds "something." The `<T>` is a placeholder for "whatever type you decide to use."

---

## 💡 Why do we need it in Android?

Android is full of generics: `LiveData<User>`, `Flow<List<Product>>`, `Response<ApiResult>`, `Adapter<ViewHolder>`. Without generics, you'd either lose type safety or write duplicate code for every type.

```kotlin
// Without generics - duplicate code everywhere 😫
class IntCache {
    private val map = mutableMapOf<String, Int>()
    fun put(key: String, value: Int) { map[key] = value }
    fun get(key: String): Int? = map[key]
}

class UserCache {
    private val map = mutableMapOf<String, User>()
    fun put(key: String, value: User) { map[key] = value }
    fun get(key: String): User? = map[key]
}
// Copy-paste for every type? No thanks! 🙅

// With generics - one class for all types! 🎉
class Cache<K, V> {
    private val map = mutableMapOf<K, V>()
    fun put(key: K, value: V) { map[key] = value }
    fun get(key: K): V? = map[key]
}

val intCache = Cache<String, Int>()
val userCache = Cache<String, User>()
```

---

## 📌 Key Concepts

- **Type Parameter (`<T>`)**: A placeholder for a type, decided when you use the class/function
- **Generic Class**: A class with type parameters (e.g., `Cache<K, V>`)
- **Generic Function**: A function with its own type parameters (e.g., `fun <T> sort(list: List<T>)`)
- **Type Inference**: Kotlin often figures out the type automatically (no need to specify)
- **Multiple Type Parameters**: Classes can have multiple (e.g., `Map<K, V>`, `Pair<A, B>`)
- **Generic Constraints**: Restrict what types are allowed (covered in advanced generics)

---

## ✍️ Syntax

```kotlin
// ============================================
// GENERIC CLASS - works with any types K and V
// ============================================

class Cache<K, V> {
    private val storage = mutableMapOf<K, V>()
    
    fun put(key: K, value: V) {
        storage[key] = value
    }
    
    fun get(key: K): V? = storage[key]
    
    fun remove(key: K): V? = storage.remove(key)
    
    fun clear() = storage.clear()
    
    fun size(): Int = storage.size
}

// Usage
fun main() {
    // Explicit type parameters
    val ageCache = Cache<String, Int>()
    ageCache.put("john", 25)
    
    // Type inference (Kotlin figures it out)
    val scoreCache = Cache<Int, Double>()
    scoreCache.put(1, 95.5)
}

// ============================================
// GENERIC FUNCTION - works with any type T
// ============================================

fun <T> T.alsoPrint(): T {
    println(this)
    return this
}

// Usage
val result = "Hello".alsoPrint() // Prints "Hello", returns "Hello"
val number = 42.alsoPrint()       // Prints "42", returns 42

// ============================================
// GENERIC SEALED CLASS
// ============================================

sealed class CacheResult<T> {
    data class Hit<T>(val value: T) : CacheResult<T>()
    data class Miss<T>(val key: Any) : CacheResult<T>()
}

// Usage
fun <K, V> Cache<K, V>.getWithResult(key: K): CacheResult<V> {
    val value = get(key)
    return if (value != null) {
        CacheResult.Hit(value)
    } else {
        CacheResult.Miss(key as Any)
    }
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Forgetting type parameter in subclass

```kotlin
sealed class Result<T> {
    // Wrong - forgot <T> on subclass!
    data class Success(val data: T) : Result<T>() // ❌ Error: T not found
}
```

### ✅ Correct way

```kotlin
sealed class Result<T> {
    // Right - subclass also has <T>
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
}
```

---

### ❌ Mistake 2: Using generic type without declaration

```kotlin
// Wrong - where does T come from?
fun printItem(item: T) { // ❌ Error: T is not defined
    println(item)
}
```

### ✅ Correct way

```kotlin
// Right - declare <T> before function name
fun <T> printItem(item: T) {
    println(item)
}
```

---

### ❌ Mistake 3: Mixing up class-level and function-level generics

```kotlin
class Box<T> {
    // Wrong - T is already defined at class level, don't redeclare
    fun <T> getItem(): T { } // Confusing! This is a DIFFERENT T
}
```

### ✅ Correct way

```kotlin
class Box<T>(private val item: T) {
    // Right - use class-level T
    fun getItem(): T = item
    
    // This is fine - different purpose, clearly named
    fun <R> transform(converter: (T) -> R): R = converter(item)
}
```

---

### ❌ Mistake 4: Expecting runtime type information

```kotlin
// Wrong - generic types are erased at runtime!
fun <T> checkType(item: Any): Boolean {
    return item is T // ❌ Error: Cannot check for erased type
}
```

### ✅ Correct way

```kotlin
// Right - use reified with inline functions
inline fun <reified T> checkType(item: Any): Boolean {
    return item is T // ✅ Works!
}

// Or pass class explicitly
fun <T : Any> checkType(item: Any, clazz: KClass<T>): Boolean {
    return clazz.isInstance(item)
}
```

---

## 🎯 Mini Task

**What we're building:**  
A type-safe caching system that stores any key-value pairs, returns results wrapped in a sealed class (Hit/Miss), and includes a handy debug extension function.

**What you'll learn:**
- How to create generic classes with multiple type parameters
- How to write generic extension functions
- How to combine generics with sealed classes for type-safe results
- How type inference makes generics easier to use
- Real-world pattern used in Android repositories and data layers

---

## 📚 Quick Recap

- **`<T>`** is a type placeholder - replaced with actual type when used
- Generic classes: `class Cache<K, V>` - K and V become real types at usage
- Generic functions: `fun <T> doSomething()` - declare `<T>` before function name
- Kotlin often **infers** generic types automatically (less typing!)
- Combine generics with **sealed classes** for type-safe success/failure patterns
- Generic types are **erased** at runtime - use `reified` with `inline` if you need runtime type info
- Common Android examples: `LiveData<T>`, `Flow<T>`, `Response<T>`, `List<T>`

**Next Topic:** Generic Constraints (limiting what types can be used with `<T : SomeType>`)