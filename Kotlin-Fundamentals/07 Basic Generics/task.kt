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


class Cache<K,V>{
    private  val storage = mutableMapOf<K,V>()
    fun put(key: K, value: V){
        storage[key]=value
    }
    fun get(key: K):V? = storage[key]
    fun remove(key: K):V? = storage.remove(key)
    fun clear() {
        storage.clear()
    }
    fun size():Int = storage.size

    fun getWithResult(key:K):CacheResult<V>{
        val value = storage[key]
        if(storage.containsKey(key)) return  CacheResult.Hit(value)
        else return CacheResult.Miss(key)
    }
}

fun <T> T.alsoPrint():T{
    println(this)
    return this
}

sealed class CacheResult<T>{
    data class Hit<T>(val value:T):CacheResult<T>()
    data class Miss<T>(val key:Any):CacheResult<T>()
}
fun main(){
    userAges = Cache<String,Int>()
    userAges.put("nirb",21)
    val state:CacheResult<T> = getWithResult("nirb")
    when(state){
        is CacheResult.Hit ->  println("found: $key -> $value")
        is CacheResult.Miss -> println("Not found")
    }
}

