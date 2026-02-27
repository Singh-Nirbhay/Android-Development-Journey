//🏗️ BUILD CHALLENGE #11 (Quick)
//Task: Compare Collection vs Sequence performance.
//
//Kotlin
//
//fun main() {
//    val bigList = (1..10_000_000).toList()
//
//    // TODO 1: Using Collection, find first 5 numbers divisible by 7 and greater than 1000
//    // Measure time using System.currentTimeMillis()
//
//    // TODO 2: Using Sequence, do the same
//    // Measure time
//
//    // TODO 3: Print both results and times
//
//    // TODO 4: Create infinite sequence of multiples of 3
//    // Take first 20
//}

fun main() {
    println("🔧 Creating big list (10 million items)...\n")
    val bigList = (1..10_000_000).toList()

    // ============================
    // COLLECTION (Eager)
    // ============================
    println("=== Using Collection (Eager) ===")
    var collectionOps = 0
    val startCollection = System.currentTimeMillis()

    val resultCollection = bigList
        .filter { collectionOps++; it > 1000 }
        .filter { collectionOps++; it % 7 == 0 }
        .take(5)

    val endCollection = System.currentTimeMillis()
    val collectionTime = endCollection - startCollection

    println("Result: $resultCollection")
    println("Time: $collectionTime ms")
    println("Operations: $collectionOps")

    // ============================
    // SEQUENCE (Lazy)
    // ============================
    println("\n=== Using Sequence (Lazy) ===")
    var sequenceOps = 0
    val startSequence = System.currentTimeMillis()

    val resultSequence = bigList
        .asSequence()
        .filter { sequenceOps++; it > 1000 }
        .filter { sequenceOps++; it % 7 == 0 }
        .take(5)
        .toList()

    val endSequence = System.currentTimeMillis()
    val sequenceTime = endSequence - startSequence

    println("Result: $resultSequence")
    println("Time: $sequenceTime ms")
    println("Operations: $sequenceOps")

    // ============================
    // COMPARISON
    // ============================
    println("\n=== Performance Comparison ===")
    if (sequenceTime > 0) {
        val speedup = collectionTime.toDouble() / sequenceTime
        println("⚡ Sequence is ${String.format("%.1f", speedup)}x faster")
    } else {
        println("⚡ Sequence too fast to measure!")
    }

    val opsReduction = ((1 - sequenceOps.toDouble() / collectionOps) * 100).toInt()
    println("📊 Sequence did $opsReduction% fewer operations")

    // ============================
    // INFINITE SEQUENCE
    // ============================
    println("\n=== Infinite Sequence (Multiples of 3) ===")
    val multiplesOfThree = generateSequence(3) { it + 3 }
    val first20 = multiplesOfThree.take(20).toList()
    println(first20)

    println("\n=== Bonus: More Infinite Sequences ===")

    // Fibonacci
    val fibonacci = generateSequence(Pair(0, 1)) { Pair(it.second, it.first + it.second) }
        .map { it.first }
    println("Fibonacci: ${fibonacci.take(10).toList()}")

    // Powers of 2
    val powersOf2 = generateSequence(1) { it * 2 }
    println("Powers of 2: ${powersOf2.take(10).toList()}")

    // Even numbers
    val evens = generateSequence(2) { it + 2 }
    println("Even numbers: ${evens.take(10).toList()}")
}