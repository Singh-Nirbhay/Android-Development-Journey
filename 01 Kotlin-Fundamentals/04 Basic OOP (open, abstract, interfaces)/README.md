# Basic OOP (open, abstract, interfaces)

> Flexible blueprints that let you build related classes with shared behavior and guaranteed contracts

---

## 🤔 What is it?

**Interfaces** define what a class must do (a contract). **Abstract classes** provide partial implementation that subclasses complete. **Open classes** allow inheritance (Kotlin classes are `final` by default).

Think of an interface as a job description ("must be able to process payments"), an abstract class as a training manual with some chapters filled in, and `open` as permission to extend or override.

---

## 💡 Why do we need it in Android?

Imagine building a payment screen that accepts credit cards, UPI, wallets, and cash. Each payment method works differently, but your UI needs a unified way to process them all.

```kotlin
// Without OOP - messy when/if chains everywhere 😫
fun processPayment(type: String, amount: Double) {
    when (type) {
        "CREDIT_CARD" -> { /* process credit card */ }
        "UPI" -> { /* process UPI */ }
        "WALLET" -> { /* process wallet */ }
        // Add new method? Change code in 10 places!
    }
}

// With OOP - clean polymorphism 🎉
interface PaymentMethod {
    fun processPayment(amount: Double): Boolean
}

fun processPayment(method: PaymentMethod, amount: Double) {
    method.processPayment(amount) // Works for ANY payment type!
}
```

This is how Android's `RecyclerView.Adapter`, `ViewModel`, and `Repository` patterns work - interfaces and abstract classes provide structure while letting you customize behavior.

---

## 📌 Key Concepts

- **Interface**: A contract that defines methods a class **must** implement (no implementation provided)
- **Abstract Class**: A partial blueprint that can have both implemented and abstract members (can't be instantiated directly)
- **Open Class/Function**: Marked with `open` keyword to allow inheritance/overriding (Kotlin's default is `final`)
- **Override**: Implementing an interface method or replacing a parent class method
- **Polymorphism**: Treating different types uniformly through a common interface/parent class
- **Abstract Property**: A property declared in an abstract class that subclasses must implement

---

## ✍️ Syntax

```kotlin
// INTERFACE - contract only, no implementation
interface PaymentMethod {
    fun processPayment(amount: Double): Boolean
    fun getMethodName(): String
}

// ABSTRACT CLASS - partial implementation
abstract class BasePayment {
    // Regular property (all subclasses inherit)
    val transactionId: String = System.currentTimeMillis().toString()
    
    // Abstract property (subclasses MUST provide)
    abstract val fee: Double
    
    // Regular method (all subclasses inherit)
    fun calculateTotal(amount: Double): Double {
        return amount + fee
    }
}

// CONCRETE CLASS - extends abstract + implements interface
class CreditCardPayment(val cardNumber: String) : 
    BasePayment(),           // Extend abstract class
    PaymentMethod {          // Implement interface
    
    // Provide the abstract property
    override val fee: Double = 2.5
    
    // Implement interface methods
    override fun processPayment(amount: Double): Boolean {
        println("Processing $amount via card")
        return true
    }
    
    override fun getMethodName(): String = "Credit Card"
}

// Usage - polymorphism in action
fun main() {
    val payment: PaymentMethod = CreditCardPayment("1234-5678")
    payment.processPayment(100.0) // Works!
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Forgetting to mark class/function as `open`

```kotlin
// Wrong - Kotlin classes are final by default!
abstract class BasePayment {
    val id: String = "123"
    fun calculate() { } // Not overridable!
}

class CreditCard : BasePayment() {
    override fun calculate() { } // ❌ Error: calculate is final
}
```

### ✅ Correct way

```kotlin
// Right - mark as 'open' to allow overriding
abstract class BasePayment {
    val id: String = "123"
    open fun calculate() { } // Can be overridden
}

class CreditCard : BasePayment() {
    override fun calculate() { } // ✅ Works!
}
```

---

### ❌ Mistake 2: Trying to instantiate an abstract class

```kotlin
// Wrong - abstract classes can't be created directly
abstract class BasePayment {
    abstract val fee: Double
}

fun main() {
    val payment = BasePayment() // ❌ Error: can't instantiate abstract
}
```

### ✅ Correct way

```kotlin
// Right - create a concrete subclass
abstract class BasePayment {
    abstract val fee: Double
}

class CreditCard : BasePayment() {
    override val fee = 2.5
}

fun main() {
    val payment = CreditCard() // ✅ Works!
}
```

---

### ❌ Mistake 3: Forgetting `override` keyword

```kotlin
interface PaymentMethod {
    fun processPayment(amount: Double): Boolean
}

// Wrong - forgot 'override'
class CreditCard : PaymentMethod {
    fun processPayment(amount: Double): Boolean { // ❌ Error
        return true
    }
}
```

### ✅ Correct way

```kotlin
interface PaymentMethod {
    fun processPayment(amount: Double): Boolean
}

// Right - use 'override' keyword
class CreditCard : PaymentMethod {
    override fun processPayment(amount: Double): Boolean {
        return true
    }
}
```

---

### ❌ Mistake 4: Confusing abstract class with interface

```kotlin
// Wrong - interface can't have constructor parameters
interface PaymentMethod(val transactionId: String) { // ❌ Error
    fun process(): Boolean
}
```

### ✅ Correct way

```kotlin
// Right - use abstract class for constructor params + state
abstract class BasePayment(val transactionId: String) {
    abstract fun process(): Boolean
}

// Or use interface if no state needed
interface PaymentMethod {
    fun process(): Boolean
}
```

---

## 🎯 Mini Task

**What we're building:**  
A flexible payment system where credit cards and UPI can be processed uniformly, with automatic fee calculation and transaction tracking.

**What you'll learn:**
- How to create interfaces to define contracts
- How to use abstract classes for shared implementation
- How to extend abstract classes and implement interfaces simultaneously
- How polymorphism lets you treat different types uniformly
- When to use interfaces vs abstract classes

---

## 📚 Quick Recap

- **Interfaces** define contracts (what classes must do) - use when there's no shared implementation
- **Abstract classes** provide partial implementation - use when subclasses share common behavior
- Mark classes/functions as `open` to allow inheritance/overriding (Kotlin's default is `final`)
- A class can implement **multiple interfaces** but extend only **one abstract class**
- Use `override` keyword when implementing interface methods or overriding parent methods
- **Polymorphism** lets you treat different types uniformly (e.g., `PaymentMethod` can be credit card, UPI, wallet)

**Next Topic:** Sealed Classes and Enums (for restricted type hierarchies and fixed sets of values)