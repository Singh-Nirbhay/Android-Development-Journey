# Nested Classes and Value Classes

> Type-safe wrappers and organized class structures that make your code safer and cleaner

---

## 🤔 What is it?

**Value classes** wrap a single value in a type-safe container without runtime overhead (the wrapper disappears at compile time). **Nested classes** are classes defined inside other classes to organize related code and create logical groupings.

Think of value classes as labeled boxes that prevent you from mixing up similar data (like mixing a product ID with a customer ID). Nested classes are like filing cabinets - they keep related things together.

---

## 💡 Why do we need it in Android?

Imagine you're building an e-commerce app. You have `String` IDs everywhere - product IDs, order IDs, customer IDs. What stops you from accidentally using a product ID where a customer ID should go? **Nothing.** The compiler can't tell them apart.

```kotlin
// Without value classes - this compiles but is WRONG! 😱
fun getCustomer(customerId: String) { }
val productId = "PROD_123"
getCustomer(productId) // Oops! Passed product ID instead of customer ID
```

Value classes solve this. Nested classes help you organize related constants and utilities (like order statuses) right inside the `Order` class instead of scattering them across files.

---

## 📌 Key Concepts

- **Value Class (`@JvmInline value class`)**: A compile-time wrapper around a single value that provides type safety with zero runtime cost
- **Nested Class**: A class defined inside another class that doesn't hold a reference to the outer class (like `static` in Java)
- **Inner Class** (not used here): A nested class with `inner` keyword that CAN access outer class members
- **Companion Object**: A singleton object inside a class - perfect for storing constants and factory methods
- **Type Safety**: The compiler prevents you from mixing up similar types (like different ID types)

---

## ✍️ Syntax

```kotlin
// VALUE CLASS - wraps a single value
@JvmInline
value class ProductId(val value: String)

@JvmInline
value class Price(val amount: Double)

// NESTED CLASS - defined inside another class
class Order {
    // This is a nested class (doesn't need 'outer' reference)
    class Status(val name: String, val code: Int)
    
    // Companion object - for shared constants/methods
    companion object {
        val PENDING = Status("Pending", 0)
        val SHIPPED = Status("Shipped", 1)
    }
}

// Usage
fun main() {
    val id = ProductId("PROD_001") // Type-safe wrapper
    val status = Order.PENDING      // Access via class name
    
    // Accessing the wrapped value
    println(id.value) // "PROD_001"
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Forgetting `@JvmInline` annotation

```kotlin
// Wrong - this is a regular class (runtime overhead!)
value class UserId(val id: String) 
```

### ✅ Correct way

```kotlin
// Right - compiler optimizes this away at runtime
@JvmInline
value class UserId(val id: String)
```

---

### ❌ Mistake 2: Trying to put multiple properties in a value class

```kotlin
// Wrong - value classes can only wrap ONE value
@JvmInline
value class Product(val id: String, val name: String) // ❌ Won't compile
```

### ✅ Correct way

```kotlin
// Right - use a regular data class for multiple properties
data class Product(val id: ProductId, val name: String)
```

---

### ❌ Mistake 3: Using `inner` when you need a simple nested class

```kotlin
// Wrong - 'inner' creates unnecessary reference to outer class
class Order {
    inner class Status(val name: String) // Uses more memory
}
```

### ✅ Correct way

```kotlin
// Right - simple nested class (no outer reference needed)
class Order {
    class Status(val name: String, val code: Int)
}
```

---

## 🎯 Mini Task

**What we're building:**  
A type-safe e-commerce order system where you can't accidentally mix up product IDs with customer IDs, and all order statuses are neatly organized inside the `Order` class.

**What you'll learn:**
- How to create value classes for type-safe IDs
- How to use nested classes to organize related constants
- How to use companion objects for shared data
- How to calculate computed properties from collections

---

## 📚 Quick Recap

- **Value classes** (`@JvmInline value class`) wrap single values for type safety with zero runtime cost
- **Nested classes** organize related code inside parent classes (like `Order.Status`)
- **Companion objects** hold shared constants and methods (accessed via class name, not instance)
- Value classes prevent bugs by making the compiler catch ID mix-ups
- Use nested classes + companion objects for constants that logically belong to a class

**Next Topic:** Sealed Classes (for restricted class hierarchies - perfect for modeling states like Success/Error)