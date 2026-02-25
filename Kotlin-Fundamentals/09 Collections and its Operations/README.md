# Collections Core Operations

> Transform, filter, and analyze data with powerful one-liners that replace dozens of loops

---

## 🤔 What is it?

**Collection operations** are built-in functions that let you manipulate lists, sets, and maps without writing manual loops. Filter items, transform data, find elements, sort, group - all with readable, chainable functions.

Think of them as assembly line machines: data goes in, gets processed, transformed data comes out. Chain them together for complex operations in a single expression.

---

## 💡 Why do we need it in Android?

Every Android app processes collections: filtering search results, transforming API responses, sorting user lists, validating form data. Without collection operations, you'd write verbose loops everywhere.

```kotlin
// Without collection operations 😫
val passingStudents = mutableListOf<Student>()
for (student in students) {
    if (student.score >= 40) {
        passingStudents.add(student)
    }
}
val names = mutableListOf<String>()
for (student in passingStudents) {
    names.add(student.name)
}

// With collection operations 🎉
val names = students
    .filter { it.score >= 40 }
    .map { it.name }
```

This is how you process RecyclerView data, filter Room database results, and transform Retrofit responses.

---

## 📌 Key Concepts

- **`filter`**: Keep only items matching a condition
- **`map`**: Transform each item into something else
- **`find` / `firstOrNull`**: Get first item matching condition (or null)
- **`all` / `any` / `none`**: Check if condition applies to all/some/no items
- **`sortedBy` / `sortedByDescending`**: Sort items by a property
- **`take` / `drop`**: Get first N items / skip first N items
- **`distinct`**: Remove duplicates
- **`count`**: Count items (optionally matching condition)
- **`groupBy`**: Group items into a map by key

---

## ✍️ Syntax

```kotlin
data class Student(
    val id: String,
    val name: String,
    val score: Int,
    val subject: String
)

fun main() {
    val students = listOf(
        Student("1", "Alice", 85, "Math"),
        Student("2", "Bob", 72, "Science"),
        Student("3", "Charlie", 35, "Math"),
        Student("4", "Diana", 91, "English")
    )

    // ============================================
    // FILTERING - Keep matching items
    // ============================================
    
    // filter: Keep items where condition is true
    val passing = students.filter { it.score >= 40 }
    
    // filterNot: Keep items where condition is false
    val failing = students.filterNot { it.score >= 40 }

    // ============================================
    // TRANSFORMING - Change items
    // ============================================
    
    // map: Transform each item
    val names: List<String> = students.map { it.name }
    
    // mapNotNull: Transform and remove nulls
    val upperNames = students.mapNotNull { it.name.uppercase() }

    // ============================================
    // FINDING - Get specific items
    // ============================================
    
    // find / firstOrNull: First matching item
    val firstFail = students.find { it.score < 40 }
    
    // last / lastOrNull: Last matching item
    val lastPass = students.lastOrNull { it.score >= 40 }
    
    // first / last: Throws if not found (use carefully!)
    val topStudent = students.maxByOrNull { it.score }

    // ============================================
    // CHECKING - Boolean results
    // ============================================
    
    // all: Every item matches?
    val allPassing = students.all { it.score >= 40 }  // false
    
    // any: At least one matches?
    val anyFailing = students.any { it.score < 40 }   // true
    
    // none: No items match?
    val nonePerfect = students.none { it.score == 100 }  // true

    // ============================================
    // SORTING - Order items
    // ============================================
    
    // sortedBy: Ascending order
    val byScoreAsc = students.sortedBy { it.score }
    
    // sortedByDescending: Descending order
    val byScoreDesc = students.sortedByDescending { it.score }

    // ============================================
    // SLICING - Get portions
    // ============================================
    
    // take: First N items
    val top3 = students.sortedByDescending { it.score }.take(3)
    
    // drop: Skip first N items
    val afterFirst2 = students.drop(2)
    
    // takeLast / dropLast: From the end
    val last2 = students.takeLast(2)

    // ============================================
    // AGGREGATING - Compute values
    // ============================================
    
    // count: Number of items
    val mathCount = students.count { it.subject == "Math" }
    
    // sumOf: Sum a property
    val totalScore = students.sumOf { it.score }
    
    // average: Mean value
    val avgScore = students.map { it.score }.average()
    
    // maxOf / minOf: Extreme values
    val highestScore = students.maxOf { it.score }

    // ============================================
    // GROUPING & DISTINCT
    // ============================================
    
    // distinct: Remove duplicates
    val subjects = students.map { it.subject }.distinct()
    
    // distinctBy: Remove duplicates by property
    val uniqueBySubject = students.distinctBy { it.subject }
    
    // groupBy: Create map of lists
    val bySubject: Map<String, List<Student>> = students.groupBy { it.subject }
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Using `first()` when item might not exist

```kotlin
val students = listOf<Student>()

// Wrong - throws NoSuchElementException!
val first = students.first { it.score > 90 } // 💥 Crash!
```

### ✅ Correct way

```kotlin
// Right - returns null if not found
val first = students.firstOrNull { it.score > 90 }

// Or use find (same as firstOrNull)
val first = students.find { it.score > 90 }

// Handle the null
first?.let { println("Found: ${it.name}") }
    ?: println("No student found")
```

---

### ❌ Mistake 2: Forgetting `it` in single-parameter lambdas

```kotlin
// Wrong - what is 'score'?
val passing = students.filter { score > 40 } // ❌ Error
```

### ✅ Correct way

```kotlin
// Right - use 'it' for single parameter
val passing = students.filter { it.score > 40 }

// Or name the parameter explicitly
val passing = students.filter { student -> student.score > 40 }
```

---

### ❌ Mistake 3: Mutating during iteration

```kotlin
val students = mutableListOf(/* ... */)

// Wrong - ConcurrentModificationException!
for (student in students) {
    if (student.score < 40) {
        students.remove(student) // 💥 Crash!
    }
}
```

### ✅ Correct way

```kotlin
// Right - filter creates new list
val passing = students.filter { it.score >= 40 }

// Or use removeAll for mutation
students.removeAll { it.score < 40 }
```

---

### ❌ Mistake 4: Chaining without understanding return types

```kotlin
val students = listOf(/* ... */)

// Wrong - filter returns List, not Student
val topStudent = students.filter { it.score > 80 }.name // ❌ Error
```

### ✅ Correct way

```kotlin
// Right - get single item first, then access property
val topStudent = students.filter { it.score > 80 }.firstOrNull()?.name

// Or chain properly
val topNames = students
    .filter { it.score > 80 }
    .map { it.name }  // Now List<String>
```

---

### ❌ Mistake 5: Using `sorted()` on original list expecting mutation

```kotlin
val students = mutableListOf(/* ... */)

// Wrong - sorted() returns NEW list, doesn't modify original
students.sortedBy { it.score }
println(students) // Still in original order!
```

### ✅ Correct way

```kotlin
// Right - assign result to new variable
val sorted = students.sortedBy { it.score }

// Or use sortBy for in-place mutation (MutableList only)
students.sortBy { it.score } // Modifies students directly
```

---

## 📊 Quick Reference Table

| Operation | Returns | Purpose |
|-----------|---------|---------|
| `filter { }` | `List<T>` | Keep matching items |
| `map { }` | `List<R>` | Transform items |
| `find { }` | `T?` | First match or null |
| `all { }` | `Boolean` | All match? |
| `any { }` | `Boolean` | Any match? |
| `none { }` | `Boolean` | None match? |
| `count { }` | `Int` | Count matching |
| `sortedBy { }` | `List<T>` | Sort ascending |
| `sortedByDescending { }` | `List<T>` | Sort descending |
| `take(n)` | `List<T>` | First n items |
| `drop(n)` | `List<T>` | Skip first n items |
| `distinct()` | `List<T>` | Remove duplicates |
| `groupBy { }` | `Map<K, List<T>>` | Group by key |
| `sumOf { }` | `Number` | Sum values |
| `maxByOrNull { }` | `T?` | Item with max value |

---

## 🎯 Mini Task

**What we're building:**  
A student grade processor that filters top performers, finds failures, calculates statistics, and organizes data by subject - all using collection operations.

**What you'll learn:**
- How to chain multiple operations fluently
- When to use `filter` vs `find` vs `firstOrNull`
- How to transform data with `map`
- How to check conditions with `all`, `any`, `none`
- How to sort, slice, and aggregate collections
- Real patterns used in Android data processing

---

## 📚 Quick Recap

- **`filter`** keeps items, **`map`** transforms items, **`find`** gets first match
- Use **`firstOrNull`/`find`** instead of `first` to avoid crashes on empty results
- **`all`/`any`/`none`** return booleans for condition checking
- **`sortedBy`** returns NEW list, **`sortBy`** mutates original (MutableList)
- **`take(n)`** gets first n, **`drop(n)`** skips first n
- **`distinct`** removes duplicates, **`groupBy`** creates map of lists
- Chain operations: `list.filter { }.map { }.sortedBy { }.take(3)`
- These operations are **lazy-friendly** - use `asSequence()` for large collections

**Next Topic:** Advanced Collection Operations (flatMap, partition, associate, fold/reduce)