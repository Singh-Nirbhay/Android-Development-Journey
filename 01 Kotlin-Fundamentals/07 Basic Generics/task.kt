//🏗️ BUILD CHALLENGE #7
//Create a Generic Cache System:
//
//Step 1: Create a generic class Cache<K, V>:
//
//    K = Key type
//V = Value type
//Private storage: MutableMap<K, V>
//fun put(key: K, value: V)
//fun get(key: K): V?
//fun remove(key: K): V?
//fun clear()
//fun size(): Int
//Step 2: Create a generic function:
//
//Kotlin
//
//fun <T> T.alsoPrint(): T
//Prints the value
//Returns the same value
//(Useful for debugging)
//Step 3: Create a generic sealed class CacheResult<T>:
//
//    data class Hit<T>(val value: T) — Cache found value
//data class Miss<T>(val key: Any) — Cache didn't find value
//Step 4: Add a function to Cache:
//
//Kotlin
//
//fun getWithResult(key: K): CacheResult<V>
//Returns Hit if key exists
//Returns Miss if key doesn't exist
//Step 5: In main():
//
//Create Cache<String, Int> for user ages
//Put some values
//Get value using getWithResult and handle with when
//Use alsoPrint() extension

// ----------------------------
// Generic Cache Class
// ----------------------------
class Cache<K, V> {
    private val storage = mutableMapOf<K, V>()

    fun put(key: K, value: V) {
        storage[key] = value
    }

    fun get(key: K): V? = storage[key]

    fun remove(key: K): V? = storage.remove(key)

    fun clear() = storage.clear()

    fun size() = storage.size

    fun getWithResult(key: K): CacheResult<V> {
        val value = storage[key]
        return if (storage.containsKey(key)) {
            CacheResult.Hit(value!!)
        } else {
            CacheResult.Miss(key as Any)
        }
    }
}

// ----------------------------
// Generic Extension Function
// ----------------------------
fun <T> T.alsoPrint(): T {
    println("🔍 Value: $this")
    return this
}

// ----------------------------
// Generic Sealed Class
// ----------------------------
sealed class CacheResult<T> {
    data class Hit<T>(val value: T) : CacheResult<T>()
    data class Miss<T>(val key: Any) : CacheResult<T>()
}

// ----------------------------
// Main
// ----------------------------
fun main() {
    println("=== CACHE TEST ===\n")

    val userAges = Cache<String, Int>()

    // Put values
    userAges.put("nirbhay", 21)
    userAges.put("john", 25)
    userAges.put("jane", 30)

    println("Cache size: ${userAges.size()}")

    // Test alsoPrint()
    println("\n--- Using alsoPrint() ---")
    val age = userAges.get("nirbhay").alsoPrint()
    println("Retrieved age: $age")

    // Test getWithResult — Hit case
    println("\n--- Cache Hit ---")
    val result1 = userAges.getWithResult("nirbhay")
    when (result1) {
        is CacheResult.Hit -> println("✅ Found: ${result1.value}")
        is CacheResult.Miss -> println("❌ Not found: ${result1.key}")
    }

    // Test getWithResult — Miss case
    println("\n--- Cache Miss ---")
    val result2 = userAges.getWithResult("unknown")
    when (result2) {
        is CacheResult.Hit -> println("✅ Found: ${result2.value}")
        is CacheResult.Miss -> println("❌ Not found for key: ${result2.key}")
    }

    // Test remove
    println("\n--- Remove ---")
    val removed = userAges.remove("john")
    println("Removed john's age: $removed")
    println("Cache size after remove: ${userAges.size()}")

    // Test clear
    println("\n--- Clear ---")
    userAges.clear()
    println("Cache size after clear: ${userAges.size()}")
}
