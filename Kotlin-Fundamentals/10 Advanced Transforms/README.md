# Collection Advanced Transforms

> Flatten nested data, split lists, build maps, and aggregate values like a data wizard

---

## 🤔 What is it?

**Advanced collection transforms** go beyond basic filter/map operations. They reshape data structures: flatten nested lists, split collections in two, convert lists to maps, and combine values with accumulators.

Think of them as power tools for data manipulation - when you need to restructure, reorganize, or recalculate complex data, these operations do the heavy lifting.

---

## 💡 Why do we need it in Android?

Real Android apps deal with nested data constantly: orders containing items, users with multiple addresses, API responses with nested objects. You need to flatten, group, index, and aggregate this data efficiently.

```kotlin
// Without advanced transforms 😫
val allItems = mutableListOf<OrderItem>()
for (order in orders) {
    for (item in order.items) {
        allItems.add(item) // Manual nested loop
    }
}

var totalRevenue = 0.0
for (order in orders) {
    for (item in order.items) {
        totalRevenue += item.price * item.quantity // More nesting
    }
}

// With advanced transforms 🎉
val allItems = orders.flatMap { it.items }

val totalRevenue = orders
    .flatMap { it.items }
    .sumOf { it.price * it.quantity }
```

This is how you process Firestore subcollections, Room database relations, and REST API nested responses.

---

## 📌 Key Concepts

- **`flatMap`**: Transform each item into a list, then flatten into single list
- **`groupBy`**: Organize items into a map where key → list of matching items
- **`associateBy`**: Create a map where each item becomes a value with a unique key
- **`partition`**: Split collection into two lists based on a condition (true/false)
- **`fold`/`reduce`**: Combine all items into a single value using an accumulator
- **`associate`**: Build a custom key-value map from collection items
- **`zip`**: Combine two lists into pairs

---

## ✍️ Syntax

```kotlin
data class Order(
    val id: String,
    val customerId: String,
    val status: String,
    val items: List<OrderItem>
)

data class OrderItem(
    val productName: String,
    val price: Double,
    val quantity: Int
)

fun main() {
    val orders = listOf(/* ... */)

    // ============================================
    // flatMap - Flatten nested collections
    // ============================================
    
    // Transform each order to its items list, then flatten
    val allItems: List<OrderItem> = orders.flatMap { it.items }
    // orders: [Order1(items=[Item1, Item2]), Order2(items=[Item3])]
    // result: [Item1, Item2, Item3]
    
    // Compare with map (returns nested list)
    val nestedItems: List<List<OrderItem>> = orders.map { it.items }
    // [List[Item1, Item2], List[Item3]]

    // ============================================
    // groupBy - Organize into map of lists
    // ============================================
    
    // Group orders by status
    val byStatus: Map<String, List<Order>> = orders.groupBy { it.status }
    // {
    //   "PENDING" -> [Order2, Order5],
    //   "SHIPPED" -> [Order3],
    //   "DELIVERED" -> [Order1, Order4]
    // }
    
    // Access groups
    val pendingOrders = byStatus["PENDING"] ?: emptyList()
    
    // Print groups
    byStatus.forEach { (status, ordersList) ->
        println("$status: ${ordersList.size} orders")
    }

    // ============================================
    // associateBy - Create lookup map
    // ============================================
    
    // Map order ID to Order object
    val ordersById: Map<String, Order> = orders.associateBy { it.id }
    // { "O1" -> Order1, "O2" -> Order2, "O3" -> Order3 }
    
    // Fast lookup
    val order = ordersById["O1"]
    
    // With custom value
    val orderTotals: Map<String, Double> = orders.associateBy(
        keySelector = { it.id },
        valueTransform = { order -> 
            order.items.sumOf { it.price * it.quantity }
        }
    )
    // { "O1" -> 840.0, "O2" -> 625.0 }

    // ============================================
    // associate - Build custom map
    // ============================================
    
    // Create ID -> Customer mapping
    val customerByOrderId: Map<String, String> = orders.associate { 
        it.id to it.customerId 
    }
    // { "O1" -> "C1", "O2" -> "C2" }

    // ============================================
    // partition - Split into two lists
    // ============================================
    
    // Split by condition (returns Pair)
    val (delivered, notDelivered) = orders.partition { 
        it.status == "DELIVERED" 
    }
    // delivered: [Order1, Order4]
    // notDelivered: [Order2, Order3, Order5]
    
    println("Delivered: ${delivered.size}")
    println("Not delivered: ${notDelivered.size}")

    // ============================================
    // fold - Accumulate with initial value
    // ============================================
    
    // Calculate total revenue starting from 0.0
    val totalRevenue = orders.fold(0.0) { accumulator, order ->
        val orderTotal = order.items.sumOf { it.price * it.quantity }
        accumulator + orderTotal
    }
    
    // Build string with fold
    val orderSummary = orders.fold("Orders: ") { acc, order ->
        acc + "${order.id}, "
    }

    // ============================================
    // reduce - Like fold, but no initial value
    // ============================================
    
    val numbers = listOf(1, 2, 3, 4)
    
    // reduce - first item is initial accumulator
    val sum = numbers.reduce { acc, num -> acc + num }
    // Step 1: acc=1, num=2 → 3
    // Step 2: acc=3, num=3 → 6
    // Step 3: acc=6, num=4 → 10

    // ============================================
    // zip - Combine two lists into pairs
    // ============================================
    
    val ids = listOf("O1", "O2", "O3")
    val statuses = listOf("PENDING", "SHIPPED", "DELIVERED")
    
    val pairs = ids.zip(statuses)
    // [(O1, PENDING), (O2, SHIPPED), (O3, DELIVERED)]
    
    // With transform
    val statusMap = ids.zip(statuses) { id, status -> 
        id to status 
    }.toMap()
    // {"O1" -> "PENDING", "O2" -> "SHIPPED"}
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Using `map` instead of `flatMap` for nested lists

```kotlin
val orders = listOf(
    Order("O1", "C1", "PENDING", listOf(Item("A", 10.0, 1))),
    Order("O2", "C2", "SHIPPED", listOf(Item("B", 20.0, 1)))
)

// Wrong - returns List<List<OrderItem>>
val items = orders.map { it.items }
// [[Item("A")], [Item("B")]]
items.forEach { println(it.productName) } // ❌ Error: List doesn't have productName
```

### ✅ Correct way

```kotlin
// Right - returns List<OrderItem>
val items = orders.flatMap { it.items }
// [Item("A"), Item("B")]
items.forEach { println(it.productName) } // ✅ Works!
```

---

### ❌ Mistake 2: Confusing `groupBy` with `associateBy`

```kotlin
val orders = listOf(
    Order("O1", "C1", "PENDING", items),
    Order("O2", "C1", "SHIPPED", items)  // Same customer!
)

// Wrong - associateBy keeps only LAST matching item
val byCustomer = orders.associateBy { it.customerId }
// {"C1" -> Order("O2")} - Order O1 is lost!
```

### ✅ Correct way

```kotlin
// Right - groupBy keeps ALL matching items
val byCustomer = orders.groupBy { it.customerId }
// {"C1" -> [Order("O1"), Order("O2")]}

// Use associateBy only for unique keys
val byId = orders.associateBy { it.id }
// {"O1" -> Order1, "O2" -> Order2} - IDs are unique
```

---

### ❌ Mistake 3: Using `reduce` on empty list

```kotlin
val empty = emptyList<Int>()

// Wrong - crashes on empty list!
val sum = empty.reduce { acc, num -> acc + num } // 💥 Exception!
```

### ✅ Correct way

```kotlin
// Right - fold with initial value (safe for empty)
val sum = empty.fold(0) { acc, num -> acc + num }
// Returns 0 for empty list

// Or use sumOf (even better)
val sum = empty.sumOf { it }
```

---

### ❌ Mistake 4: Ignoring the accumulator in `fold`

```kotlin
val numbers = listOf(1, 2, 3, 4)

// Wrong - doesn't use accumulator!
val sum = numbers.fold(0) { acc, num -> 
    num  // ❌ Returns only last number (4)
}
```

### ✅ Correct way

```kotlin
// Right - combine accumulator with current value
val sum = numbers.fold(0) { acc, num -> 
    acc + num  // ✅ Accumulates properly
}
// 0 + 1 = 1
// 1 + 2 = 3
// 3 + 3 = 6
// 6 + 4 = 10
```

---

### ❌ Mistake 5: Not destructuring `partition` result

```kotlin
val orders = listOf(/* ... */)

// Wrong - partition returns Pair, not List
val result = orders.partition { it.status == "DELIVERED" }
val delivered = result.filter { /* ... */ } // ❌ Error: Pair has no filter
```

### ✅ Correct way

```kotlin
// Right - destructure into two lists
val (delivered, notDelivered) = orders.partition { 
    it.status == "DELIVERED" 
}

// Or access via first/second
val result = orders.partition { it.status == "DELIVERED" }
val delivered = result.first
val notDelivered = result.second
```

---

## 📊 Quick Reference Table

| Operation | Input | Output | Use Case |
|-----------|-------|--------|----------|
| `flatMap { }` | `List<List<T>>` | `List<T>` | Flatten nested lists |
| `groupBy { }` | `List<T>` | `Map<K, List<T>>` | Group by property |
| `associateBy { }` | `List<T>` | `Map<K, T>` | Index by unique key |
| `associate { }` | `List<T>` | `Map<K, V>` | Build custom map |
| `partition { }` | `List<T>` | `Pair<List<T>, List<T>>` | Split by condition |
| `fold(init) { }` | `List<T>` | `R` | Accumulate with initial |
| `reduce { }` | `List<T>` | `T` | Accumulate without initial |
| `zip(other)` | `List<A>`, `List<B>` | `List<Pair<A,B>>` | Combine two lists |

---

## 🧠 When to Use What?

```kotlin
// Need all items from nested structure? → flatMap
val allItems = orders.flatMap { it.items }

// Need to organize by category? → groupBy
val byStatus = orders.groupBy { it.status }

// Need fast lookup by ID? → associateBy
val byId = orders.associateBy { it.id }

// Need to split into pass/fail? → partition
val (passed, failed) = students.partition { it.score >= 40 }

// Need to calculate total/sum/product? → fold or sumOf
val total = orders.fold(0.0) { sum, order -> sum + order.total }
val total = orders.sumOf { it.total } // Simpler for sums

// Need to combine values step-by-step? → reduce
val product = numbers.reduce { acc, num -> acc * num }
```

---

## 🎯 Mini Task

**What we're building:**  
A complete e-commerce analytics dashboard that flattens orders into items, groups by status, creates lookup maps, splits delivery states, calculates revenue, and finds top products.

**What you'll learn:**
- How to flatten nested data structures with `flatMap`
- When to use `groupBy` vs `associateBy`
- How to split collections with `partition`
- How to accumulate values with `fold`
- How these operations work together for real analytics
- Patterns used in Android dashboards and reporting

---

## 📚 Quick Recap

- **`flatMap`** flattens nested lists into single list (orders → items)
- **`groupBy`** creates map where key → list of matching items (group by status)
- **`associateBy`** creates map where key → single item (must be unique key)
- **`partition`** splits into two lists based on condition (returns `Pair`)
- **`fold`** accumulates with initial value (safe for empty lists)
- **`reduce`** accumulates without initial (crashes on empty - use carefully)
- Destructure `partition`: `val (yes, no) = list.partition { condition }`
- Chain operations: `.flatMap { }.groupBy { }.mapValues { }`

**Next Topic:** Sequences (lazy evaluation for large collections - process millions without creating intermediate lists)