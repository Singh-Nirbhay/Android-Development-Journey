# Variance (in, out)

> Control how generic types behave with inheritance - produce with `out`, consume with `in`

---

## 🤔 What is it?

**Variance** defines how generic types relate when their type parameters are related. If `Dog` extends `Animal`, should `List<Dog>` be usable where `List<Animal>` is expected? Variance answers this.

- **`out`** (covariant): The generic type **produces** T - you can read but not write
- **`in`** (contravariant): The generic type **consumes** T - you can write but not read as T

Think of it like this: A **producer** (out) is like a vending machine that gives you items. A **consumer** (in) is like a trash can that accepts items.

---

## 💡 Why do we need it in Android?

In Android, you constantly pass around `LiveData<User>`, `Flow<List<Message>>`, and `Callback<Response>`. Without variance, you'd face frustrating type mismatches even when it should logically work.

```kotlin
// Without variance understanding 😫
open class Animal
class Dog : Animal()

fun printAnimals(animals: List<Animal>) {
    animals.forEach { println(it) }
}

val dogs: List<Dog> = listOf(Dog(), Dog())
printAnimals(dogs) // Does this work? 🤔

// YES! Because List is declared as List<out E>
// It PRODUCES elements, never consumes them

// But what about MutableList?
fun addAnimal(animals: MutableList<Animal>) {
    animals.add(Animal())
}

val dogs: MutableList<Dog> = mutableListOf()
addAnimal(dogs) // ❌ ERROR! And rightfully so!
// If allowed, we'd add Animal to a Dog list - type unsafe!
```

---

## 📌 Key Concepts

- **Invariant** (default): `Class<T>` - no relationship between `Class<Dog>` and `Class<Animal>`
- **Covariant (`out`)**: `Class<out T>` - `Class<Dog>` IS-A `Class<Animal>` (producer)
- **Contravariant (`in`)**: `Class<in T>` - `Class<Animal>` IS-A `Class<Dog>` (consumer)
- **Producer**: Only returns T, never takes T as parameter (safe to use subtype)
- **Consumer**: Only takes T as parameter, never returns T (safe to use supertype)
- **PECS**: Producer Extends, Consumer Super (Java) = Producer Out, Consumer In (Kotlin)

---

## ✍️ Syntax

```kotlin
// ============================================
// OUT - Covariance (Producer)
// ============================================

// Can ONLY produce T, never consume
interface Source<out T> {
    fun next(): T          // ✅ OK - returns T (produces)
    // fun add(item: T)    // ❌ ERROR - takes T (consumes)
}

open class Animal
class Dog : Animal()
class Cat : Animal()

fun printAll(source: Source<Animal>) {
    println(source.next())
}

fun main() {
    val dogSource: Source<Dog> = object : Source<Dog> {
        override fun next(): Dog = Dog()
    }
    
    printAll(dogSource) // ✅ Works! Source<Dog> IS-A Source<Animal>
}

// ============================================
// IN - Contravariance (Consumer)
// ============================================

// Can ONLY consume T, never produce
interface Sink<in T> {
    fun add(item: T)       // ✅ OK - takes T (consumes)
    // fun get(): T        // ❌ ERROR - returns T (produces)
}

fun fillWithDogs(sink: Sink<Dog>) {
    sink.add(Dog())
    sink.add(Dog())
}

fun main() {
    val animalSink: Sink<Animal> = object : Sink<Animal> {
        override fun add(item: Animal) { println("Added $item") }
    }
    
    fillWithDogs(animalSink) // ✅ Works! Sink<Animal> IS-A Sink<Dog>
}

// ============================================
// REAL WORLD EXAMPLE
// ============================================

// Kotlin's List is covariant (out)
public interface List<out E> {
    fun get(index: Int): E  // Only produces
}

// Kotlin's Comparable is contravariant (in)
public interface Comparable<in T> {
    fun compareTo(other: T): Int  // Only consumes
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Using `out` when you need to consume

```kotlin
// Wrong - can't add items with 'out'
interface Repository<out T> {
    fun getAll(): List<T>      // ✅ OK
    fun save(item: T)          // ❌ ERROR: T is 'out' but used in 'in' position
}
```

### ✅ Correct way

```kotlin
// Right - remove 'out' if you need both produce AND consume
interface Repository<T> {
    fun getAll(): List<T>      // ✅ OK
    fun save(item: T)          // ✅ OK
}

// Or split into separate interfaces
interface ReadRepository<out T> {
    fun getAll(): List<T>
}

interface WriteRepository<in T> {
    fun save(item: T)
}
```

---

### ❌ Mistake 2: Using `in` when you need to produce

```kotlin
// Wrong - can't return items with 'in'
interface Factory<in T> {
    fun create(): T  // ❌ ERROR: T is 'in' but used in 'out' position
}
```

### ✅ Correct way

```kotlin
// Right - use 'out' for factories/producers
interface Factory<out T> {
    fun create(): T  // ✅ OK
}
```

---

### ❌ Mistake 3: Expecting MutableList to be covariant

```kotlin
open class Animal
class Dog : Animal()

fun processAnimals(list: MutableList<Animal>) {
    list.add(Animal())
}

fun main() {
    val dogs: MutableList<Dog> = mutableListOf()
    processAnimals(dogs) // ❌ ERROR - and that's CORRECT!
    // If allowed, we'd corrupt the Dog list with a generic Animal
}
```

### ✅ Correct way

```kotlin
// Right - use List (immutable, covariant) if you only read
fun processAnimals(list: List<Animal>) {
    list.forEach { println(it) }
}

fun main() {
    val dogs: List<Dog> = listOf(Dog(), Dog())
    processAnimals(dogs) // ✅ Works!
}
```

---

## 📊 Quick Reference Table

| Keyword | Name | Direction | Example | Use When |
|---------|------|-----------|---------|----------|
| `out` | Covariant | Produces T | `Source<out T>` | Only returning T |
| `in` | Contravariant | Consumes T | `Sink<in T>` | Only accepting T |
| (none) | Invariant | Both | `MutableList<T>` | Reading AND writing T |

---

## 🧠 Memory Trick

```
OUT = OUTPUT = Returns/Produces = Subtype OK (Dog → Animal)
IN  = INPUT  = Accepts/Consumes = Supertype OK (Animal → Dog)
```

Or remember: **POCI** - **P**roducer **O**ut, **C**onsumer **I**n

---

## ❓ Q&A Section

### **Q1: Why is `List` covariant but `MutableList` invariant?**

**Answer:** `List` only produces elements (read-only), so it's safe to treat `List<Dog>` as `List<Animal>`. `MutableList` both produces AND consumes (you can add elements), so allowing `MutableList<Dog>` where `MutableList<Animal>` is expected would let you add a `Cat` to a `Dog` list!

```kotlin
// If this were allowed (it's NOT):
val dogs: MutableList<Dog> = mutableListOf(Dog())
val animals: MutableList<Animal> = dogs // ❌ Compiler prevents this
animals.add(Cat()) // Would corrupt dogs list!
val dog: Dog = dogs[0] // ClassCastException! It's actually a Cat!
```

---

### **Q2: When should I use `out` vs `in` in my own classes?**

**Answer:**
- Use **`out`** when your class only **returns/produces** T (like `Factory<out T>`, `Provider<out T>`)
- Use **`in`** when your class only **accepts/consumes** T (like `Comparator<in T>`, `Consumer<in T>`)
- Use **neither** when your class both reads and writes T (like `MutableList<T>`)

```kotlin
// Producer - only outputs T
interface EventSource<out T> {
    fun getLatestEvent(): T
    fun getAllEvents(): List<T>
}

// Consumer - only inputs T
interface EventHandler<in T> {
    fun handle(event: T)
    fun handleAll(events: List<T>)
}

// Both - no variance
interface EventStore<T> {
    fun save(event: T)
    fun load(): T
}
```

---

### **Q3: What does "declaration-site variance" mean?**

**Answer:** In Kotlin, you declare variance at the class/interface level (`class Box<out T>`), not at every usage site. This is cleaner than Java's use-site variance (`? extends T`).

```kotlin
// Kotlin - declaration-site (cleaner)
interface Source<out T> {
    fun next(): T
}

// Java equivalent - use-site (verbose)
// interface Source<T> {
//     T next();
// }
// void process(Source<? extends Animal> source) { }
```

---

### **Q4: Can I use both `in` and `out` on different type parameters?**

**Answer:** Absolutely! `Function1<in P1, out R>` does exactly this.

```kotlin
// Kotlin's Function1 interface
interface Function1<in P1, out R> {
    operator fun invoke(p1: P1): R
}

// P1 is consumed (parameter) → 'in'
// R is produced (return) → 'out'

val dogToString: (Dog) -> String = { dog -> dog.toString() }
val animalToAny: (Animal) -> Any = dogToString // ❌ Doesn't work

val animalToString: (Animal) -> String = { it.toString() }
val dogToAny: (Dog) -> Any = animalToString // ✅ Works!
```

---

### **Q5: Why does `Comparable<in T>` use `in`?**

**Answer:** `Comparable` consumes T - it takes another T to compare against. If you have `Comparable<Animal>`, it can compare against any Animal, including Dogs. So it's safe to use where `Comparable<Dog>` is expected.

```kotlin
val animalComparator: Comparable<Animal> = object : Comparable<Animal> {
    override fun compareTo(other: Animal): Int = 0
}

fun sortDogs(comparator: Comparable<Dog>) {
    // Uses comparator to compare dogs
}

sortDogs(animalComparator) // ✅ Works! Animal comparator can handle Dogs
```

---

### **Q6: What's the practical impact in Android development?**

**Answer:** You encounter variance with:
- **`LiveData<out T>`** - You observe (read) values, safe to treat `LiveData<Dog>` as `LiveData<Animal>`
- **`Flow<out T>`** - Same as LiveData, you collect values
- **Callbacks** - Often contravariant: `Callback<in T>` accepts T

```kotlin
// Common Android pattern
fun observeAnimals(liveData: LiveData<Animal>) {
    liveData.observe(owner) { animal -> /* ... */ }
}

val dogLiveData: LiveData<Dog> = MutableLiveData()
observeAnimals(dogLiveData) // ✅ Works because LiveData uses 'out'
```

---

## 📚 Quick Recap

- **`out`** = Producer = Returns T = Covariant = Subtype relationship preserved
- **`in`** = Consumer = Accepts T = Contravariant = Supertype relationship preserved
- **No modifier** = Invariant = Both reads and writes = No type relationship
- `List<out E>` is covariant → `List<Dog>` usable as `List<Animal>`
- `MutableList<E>` is invariant → `MutableList<Dog>` NOT usable as `MutableList<Animal>`
- Remember **POCI**: Producer Out, Consumer In

**Next Topic:** Generic Constraints (`<T : SomeType>` for limiting what types are allowed)