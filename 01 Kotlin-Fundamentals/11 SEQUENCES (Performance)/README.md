# Sequences (Performance)

> Lazy evaluation that processes only what you need, when you need it - blazing fast for large datasets

---

## 🤔 What is it?

**Sequences** are lazy collections that process elements one-by-one through the entire chain before moving to the next element. Unlike regular collections that create intermediate lists at each step, sequences defer computation until you actually need the result.

Think of it like an assembly line vs batch processing: Collections process ALL items through step 1, then ALL through step 2. Sequences process item 1 through ALL steps, then item 2 through ALL steps.

---

## 💡 Why do we need it in Android?

Android apps deal with large datasets: thousands of database rows, API responses with hundreds of items, infinite streams of user events. Processing these with regular collections creates intermediate lists, wasting memory and CPU.

```kotlin
// Regular collection - creates 3 intermediate lists! 😫
val result = bigList
    .filter { it > 1000 }      // Creates List of 9M items
    .filter { it % 7 == 0 }    // Creates List of 1.3M items
    .take(5)                   // Creates List of 5 items
// Processed 10M items even though we only need 5!

// Sequence - processes only what's needed! 🎉
val result = bigList
    .asSequence()
    .filter { it > 1000 }
    .filter { it % 7 == 0 }
    .take(5)                   // Stops after finding 5 items
    .toList()
// Processes ~7,000 items to find the 5 we need
```

This is crucial for Room database queries, Firestore snapshots, and Flow transformations.

---

## 📌 Key Concepts

- **Lazy Evaluation**: Operations don't execute until you call a terminal operation (like `toList()`)
- **Intermediate Operations**: `filter`, `map`, `take` - return a sequence, don't process yet
- **Terminal Operations**: `toList()`, `count()`, `first()` - trigger actual processing
- **Vertical Processing**: Each element goes through ALL operations before next element
- **Horizontal Processing**: Collections process ALL elements through each operation
- **No Intermediate Collections**: Sequences don't create temporary lists between steps
- **Infinite Sequences**: Can represent endless data (prime numbers, timestamps, events)

---

## ✍️ Syntax

```kotlin
// ============================================
// BASIC SEQUENCE USAGE
// ============================================

val numbers = listOf(1, 2, 3, 4, 5)

// Convert to sequence
val sequence = numbers.asSequence()

// Chain operations (not executed yet!)
val result = sequence
    .filter { println("Filter $it"); it > 2 }
    .map { println("Map $it"); it * 2 }
    .take(2)
// Nothing printed yet - no operations executed!

// Terminal operation triggers execution
val list = result.toList()
// NOW operations execute:
// Filter 1
// Filter 2
// Filter 3
// Map 3 -> 6
// Filter 4
// Map 4 -> 8
// (Stops - already have 2 items)

// ============================================
// PERFORMANCE COMPARISON
// ============================================

val bigList = (1..10_000_000).toList()

// COLLECTION (Eager)
val collectionResult = bigList
    .filter { it > 1000 }      // Processes ALL 10M
    .filter { it % 7 == 0 }    // Processes ALL 9M
    .take(5)                   // Takes 5
// Total operations: ~19M

// SEQUENCE (Lazy)
val sequenceResult = bigList
    .asSequence()
    .filter { it > 1000 }
    .filter { it % 7 == 0 }
    .take(5)
    .toList()                  // Stops after 5 found
// Total operations: ~7,000 (stops early!)

// ============================================
// INFINITE SEQUENCES
// ============================================

// generateSequence - infinite sequence
val multiplesOf3 = generateSequence(3) { it + 3 }
// 3, 6, 9, 12, 15, ...

// MUST use take() with infinite sequences
val first10 = multiplesOf3.take(10).toList()
// [3, 6, 9, 12, 15, 18, 21, 24, 27, 30]

// Fibonacci sequence
val fibonacci = generateSequence(Pair(0, 1)) { (a, b) ->
    Pair(b, a + b)
}.map { it.first }

println(fibonacci.take(10).toList())
// [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]

// Powers of 2
val powersOf2 = generateSequence(1) { it * 2 }
println(powersOf2.take(8).toList())
// [1, 2, 4, 8, 16, 32, 64, 128]

// Natural numbers
val naturals = generateSequence(1) { it + 1 }

// ============================================
// SEQUENCE FROM ITERATOR
// ============================================

// From a lambda (called repeatedly until null)
val sequence = generateSequence {
    readLine()?.takeIf { it.isNotBlank() }
}
// Reads lines until blank line

// ============================================
// WHEN TO USE SEQUENCES
// ============================================

// ✅ Use sequence when:
// - Large datasets (thousands+ items)
// - Multiple operations chained
// - You don't need all results (take, first, find)
// - Working with infinite data

// ❌ Don't use sequence when:
// - Small datasets (< 100 items)
// - Single operation
// - Need to process all items anyway (sort, groupBy)
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Forgetting terminal operation

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Wrong - operations never execute!
val result = numbers
    .asSequence()
    .filter { println("Filtering $it"); it > 2 }
    .map { println("Mapping $it"); it * 2 }
// Nothing prints! No terminal operation
```

### ✅ Correct way

```kotlin
// Right - add terminal operation
val result = numbers
    .asSequence()
    .filter { println("Filtering $it"); it > 2 }
    .map { println("Mapping $it"); it * 2 }
    .toList()  // Terminal operation - triggers execution
// Now operations execute and print
```

---

### ❌ Mistake 2: Using sequences for small collections

```kotlin
val smallList = listOf(1, 2, 3, 4, 5)

// Wrong - overhead of sequence not worth it!
val result = smallList
    .asSequence()
    .filter { it > 2 }
    .map { it * 2 }
    .toList()
// Slower than regular collection for small data
```

### ✅ Correct way

```kotlin
// Right - use regular collections for small data
val result = smallList
    .filter { it > 2 }
    .map { it * 2 }
// Faster for small lists (< 100 items)
```

---

### ❌ Mistake 3: Not using `take()` with infinite sequences

```kotlin
// Wrong - infinite loop! 💥
val naturals = generateSequence(1) { it + 1 }
val list = naturals.toList() // Never finishes!
```

### ✅ Correct way

```kotlin
// Right - limit infinite sequence with take()
val naturals = generateSequence(1) { it + 1 }
val first100 = naturals.take(100).toList() // ✅ Stops at 100
```

---

### ❌ Mistake 4: Using sequences for operations that need all items

```kotlin
val numbers = (1..1_000_000).toList()

// Wrong - sequence provides NO benefit here
val sorted = numbers
    .asSequence()
    .sortedBy { it }  // Must process ALL items anyway
    .toList()
// Same work as regular collection
```

### ✅ Correct way

```kotlin
// Right - use sequence only when you can skip items
val first5Sorted = numbers
    .asSequence()
    .filter { it % 100 == 0 }  // Reduces items
    .sortedBy { it }
    .take(5)                    // Early exit
    .toList()

// Or just use collection for full sorting
val sorted = numbers.sortedBy { it }
```

---

### ❌ Mistake 5: Reusing sequence multiple times

```kotlin
val sequence = listOf(1, 2, 3).asSequence()
    .filter { println("Filter $it"); it > 1 }

// Wrong - executes operations twice!
val list1 = sequence.toList()  // Prints: Filter 1, Filter 2, Filter 3
val list2 = sequence.toList()  // Prints again: Filter 1, Filter 2, Filter 3
```

### ✅ Correct way

```kotlin
// Right - convert to list once and reuse
val sequence = listOf(1, 2, 3).asSequence()
    .filter { println("Filter $it"); it > 1 }

val list = sequence.toList()  // Execute once
val copy = list               // Reuse result
```

---

## 📊 Performance Comparison

| Scenario | Collection | Sequence | Winner |
|----------|-----------|----------|--------|
| 10M items, take 5 | ~19M operations | ~7K operations | 🏆 Sequence (2700x faster) |
| 100 items, full process | Fast | Overhead | 🏆 Collection |
| Filter → Map → Take | 3 intermediate lists | 0 intermediate lists | 🏆 Sequence |
| Sort entire list | Same work | Same work | 🤝 Tie |
| Infinite stream | ❌ Impossible | ✅ Works | 🏆 Sequence |

---

## 🧠 Processing Order

```kotlin
val numbers = listOf(1, 2, 3)

// COLLECTION (Horizontal) - processes ALL through each step
numbers
    .filter { println("F$it"); it > 1 }  // F1, F2, F3
    .map { println("M$it"); it * 2 }     // M2, M3
// Output: F1, F2, F3, M2, M3

// SEQUENCE (Vertical) - processes EACH through all steps
numbers.asSequence()
    .filter { println("F$it"); it > 1 }
    .map { println("M$it"); it * 2 }
    .toList()
// Output: F1, F2, M2, F3, M3
```

---

## 🎯 Mini Task

**What we're building:**  
A performance benchmark comparing collections vs sequences on a 10-million item dataset, plus infinite sequence generators for multiples, Fibonacci, and powers.

**What you'll learn:**
- How lazy evaluation drastically reduces operations
- When sequences are 1000x+ faster than collections
- How to create infinite sequences with `generateSequence`
- Why terminal operations are critical
- Real-world patterns for Android data processing

---

## 📚 Quick Recap

- **Sequences** use lazy evaluation - operations execute only when needed
- Convert with `.asSequence()`, trigger with `.toList()`, `.first()`, etc.
- **Vertical processing**: Each element through ALL operations before next element
- **Huge performance gains** when you don't need all items (`take`, `first`, `find`)
- Create **infinite sequences** with `generateSequence(seed) { nextValue }`
- MUST use `take()` or `first()` with infinite sequences
- Use sequences for **large datasets** (1000+ items) with **multiple operations**
- DON'T use for small lists (<100 items) or operations needing all items (sort, groupBy)

**Next Topic:** Extension Functions (add custom methods to existing classes without inheritance)