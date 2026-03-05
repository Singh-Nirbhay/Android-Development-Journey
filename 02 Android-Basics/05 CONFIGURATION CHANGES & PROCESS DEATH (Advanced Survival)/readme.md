# Configuration Changes & Process Death (Advanced Survival)

> Master the ultimate Android challenge - survive screen rotation AND system killing your app

---

## 🤔 What is it?

**Configuration changes** (screen rotation, language change) destroy and recreate Activities/Fragments. **Process death** happens when Android kills your app to free memory - your app looks alive, but all in-memory data is GONE.

Think of configuration change as restarting your computer. Process death is like someone pulling the power plug while you're working - everything unsaved is lost.

---

## 💡 Why do we need it in Android?

Android can **kill your app at ANY time** when it's in the background. Users don't know this happened - they see your app "resume" but all data (cart items, form inputs, scroll position) is LOST. This destroys user trust.

```kotlin
// Without proper handling - user rage! 😫
class ShoppingViewModel : ViewModel() {
    private val cartItems = mutableListOf<String>()
    
    fun addItem(item: String) {
        cartItems.add(item)  // User adds 10 items...
    }
    // Phone runs low on memory → Android kills app
    // User reopens → cartItems is empty!
    // User just lost their entire shopping cart 😡
}

// With SavedStateHandle - data survives! 🎉
class ShoppingViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val cartItems: MutableLiveData<ArrayList<String>> =
        savedStateHandle.getLiveData("cart", arrayListOf())
    
    fun addItem(item: String) {
        val current = cartItems.value ?: arrayListOf()
        current.add(item)
        cartItems.value = current  // Auto-saved!
    }
    // Process death → Data survives → User happy! 😊
}
```

This is **tested in interviews** and **critical for production apps**. Instagram, YouTube, Gmail all handle this perfectly.

---

## 📌 Key Concepts

- **Configuration Change**: Device rotation, language change → Activity destroyed and recreated
- **Process Death**: System kills app process to free memory (app appears "paused" to user)
- **SavedStateHandle**: ViewModel component that survives both config changes AND process death
- **onSaveInstanceState**: Activity/Fragment callback to save data before destruction
- **Bundle**: Key-value storage that survives both scenarios (limited to simple types)
- **ViewModel**: Survives config changes but NOT process death (unless using SavedStateHandle)
- **Don't Keep Activities**: Developer option that simulates process death EVERY time you leave app

---

## ✍️ The Survival Strategy

```
┌─────────────────────────────────────────────────────────┐
│              DATA SURVIVAL GUIDE                         │
├─────────────────────────────────────────────────────────┤
│ Scenario              │ Solution                         │
├─────────────────────────────────────────────────────────┤
│ Screen Rotation       │ ViewModel (basic)                │
│ Language Change       │ ViewModel (basic)                │
│ Process Death         │ SavedStateHandle in ViewModel    │
│ App Exit & Reopen     │ Room Database / DataStore        │
│ Logout & Login        │ Room Database / DataStore        │
└─────────────────────────────────────────────────────────┘

REMEMBER:
- ViewModel → Survives rotation ✅ | Process death ❌
- SavedStateHandle → Survives rotation ✅ | Process death ✅
- Room/DataStore → Survives EVERYTHING ✅ ✅ ✅
```

---

## ✍️ Syntax

### Part 1: ViewModel with SavedStateHandle

```kotlin
// ============================================
// VIEWMODEL - The Survivor
// ============================================
class SharedCartViewModel(
    private val savedStateHandle: SavedStateHandle  // Injected automatically
) : ViewModel() {
    
    companion object {
        private const val TAG = "CartViewModel"
        private const val KEY_CART_ITEMS = "key_cart_items"
        private const val KEY_ITEM_COUNT = "key_item_count"
    }
    
    // ━━━ OLD WAY (Dies on process death) ━━━
    // private val _cartItems = MutableLiveData<List<String>>()
    
    // ━━━ NEW WAY (Survives process death!) ━━━
    // getLiveData() returns MutableLiveData that auto-saves to Bundle
    val cartItems: MutableLiveData<ArrayList<String>> =
        savedStateHandle.getLiveData(KEY_CART_ITEMS, arrayListOf())
    
    val itemCount: MutableLiveData<Int> =
        savedStateHandle.getLiveData(KEY_ITEM_COUNT, 0)
    
    init {
        // Check if data was restored
        val restoredItems = cartItems.value
        if (!restoredItems.isNullOrEmpty()) {
            Log.d(TAG, "✅ Data RESTORED from saved state! Items: $restoredItems")
        } else {
            Log.d(TAG, "🆕 Fresh start - no saved state")
        }
    }
    
    fun addItem(name: String) {
        val currentList = cartItems.value ?: arrayListOf()
        currentList.add(name)
        // Setting .value triggers:
        //   1. LiveData notification → UI updates
        //   2. SavedStateHandle saves → Survives process death
        cartItems.value = currentList
        itemCount.value = currentList.size
        Log.d(TAG, "➕ Added: $name | Total: ${currentList.size} | Saved ✅")
    }
    
    fun clearCart() {
        cartItems.value = arrayListOf()
        itemCount.value = 0
        Log.d(TAG, "🗑️ Cart cleared | Saved ✅")
    }
    
    // Called when ViewModel is PERMANENTLY destroyed
    // NOT called on process death!
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "❌ ViewModel permanently destroyed (user exited app)")
    }
}

// ============================================
// WHY ArrayList<String> and not MutableList<String>?
// ============================================
/*
SavedStateHandle stores data in a Bundle.
Bundle can only store:
  ✅ ArrayList (implements Serializable)
  ❌ MutableList (interface, not Serializable)

This will CRASH:
  val items: MutableLiveData<MutableList<String>> = 
      savedStateHandle.getLiveData("key", mutableListOf())

This works:
  val items: MutableLiveData<ArrayList<String>> = 
      savedStateHandle.getLiveData("key", arrayListOf())
*/
```

---

### Part 2: Fragment with SavedStateHandle ViewModel

```kotlin
// ============================================
// FRAGMENT - Using Surviving ViewModel
// ============================================
class CartFragment : Fragment() {
    
    companion object {
        private const val TAG = "FragmentLifecycle"
    }
    
    // activityViewModels() automatically provides SavedStateHandle
    private val viewModel: SharedCartViewModel by activityViewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ━━━ Check if Fragment was restored ━━━
        if (savedInstanceState != null) {
            Log.d(TAG, "CartFragment: 🔄 RESTORED from saved state")
        } else {
            Log.d(TAG, "CartFragment: 🆕 Fresh creation")
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val tvCartItems = view.findViewById<TextView>(R.id.tv_cart_items)
        val tvItemCount = view.findViewById<TextView>(R.id.tv_item_count)
        val btnAddItem = view.findViewById<Button>(R.id.btn_add_item)
        val btnClearCart = view.findViewById<Button>(R.id.btn_clear_cart)
        
        // Observe cart items
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            tvCartItems.text = if (items.isNullOrEmpty()) {
                "Cart is empty"
            } else {
                "🛒 ${items.joinToString(", ")}"
            }
        }
        
        // Observe item count
        viewModel.itemCount.observe(viewLifecycleOwner) { count ->
            tvItemCount.text = "Total Items: $count"
        }
        
        // ━━━ Show toast if cart was restored ━━━
        if (savedInstanceState != null) {
            val count = viewModel.itemCount.value ?: 0
            if (count > 0) {
                Toast.makeText(
                    requireContext(),
                    "🔄 Cart restored! $count items recovered.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        // Fragment Result Listener (for dialog communication)
        childFragmentManager.setFragmentResultListener(
            "add_item_request",
            viewLifecycleOwner
        ) { _, bundle ->
            val itemName = bundle.getString("item_name", "")
            if (itemName.isNotEmpty()) {
                viewModel.addItem(itemName)
            }
        }
        
        // Button clicks
        btnAddItem.setOnClickListener {
            AddItemDialogFragment()
                .show(childFragmentManager, "AddItemDialog")
        }
        
        btnClearCart.setOnClickListener {
            if (viewModel.itemCount.value ?: 0 > 0) {
                ClearCartDialogFragment()
                    .show(childFragmentManager, "ClearCartDialog")
            }
        }
    }
}
```

---

### Part 3: Manual Save/Restore (Activity/Fragment)

```kotlin
// ============================================
// ACTIVITY - Manual State Saving
// ============================================
class MainActivity : AppCompatActivity() {
    
    private var userInput: String = ""
    private var scrollPosition: Int = 0
    
    companion object {
        private const val KEY_USER_INPUT = "user_input"
        private const val KEY_SCROLL_POS = "scroll_position"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // ━━━ Restore saved state ━━━
        if (savedInstanceState != null) {
            userInput = savedInstanceState.getString(KEY_USER_INPUT, "")
            scrollPosition = savedInstanceState.getInt(KEY_SCROLL_POS, 0)
            Log.d("MainActivity", "🔄 Restored: input='$userInput', scroll=$scrollPosition")
        }
    }
    
    // ━━━ Called BEFORE destruction ━━━
    // Triggered by:
    //   - Screen rotation
    //   - Language change
    //   - Process death (if app is in background)
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        
        outState.putString(KEY_USER_INPUT, userInput)
        outState.putInt(KEY_SCROLL_POS, scrollPosition)
        
        Log.d("MainActivity", "💾 Saved state: input='$userInput', scroll=$scrollPosition")
    }
}

// ============================================
// FRAGMENT - Manual State Saving
// ============================================
class FormFragment : Fragment() {
    
    private var emailInput: String = ""
    private var agreedToTerms: Boolean = false
    
    companion object {
        private const val KEY_EMAIL = "email"
        private const val KEY_AGREED = "agreed_terms"
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val cbTerms = view.findViewById<CheckBox>(R.id.cb_terms)
        
        // Restore state
        if (savedInstanceState != null) {
            emailInput = savedInstanceState.getString(KEY_EMAIL, "")
            agreedToTerms = savedInstanceState.getBoolean(KEY_AGREED, false)
            
            etEmail.setText(emailInput)
            cbTerms.isChecked = agreedToTerms
        }
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        
        // Save current state
        outState.putString(KEY_EMAIL, emailInput)
        outState.putBoolean(KEY_AGREED, agreedToTerms)
    }
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Using MutableList instead of ArrayList

```kotlin
// Wrong - Bundle can't store MutableList interface
class CartViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val items: MutableLiveData<MutableList<String>> =
        savedStateHandle.getLiveData("items", mutableListOf())
    // 💥 ClassCastException on restoration!
}
```

### ✅ Correct way

```kotlin
// Right - ArrayList implements Serializable
class CartViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val items: MutableLiveData<ArrayList<String>> =
        savedStateHandle.getLiveData("items", arrayListOf())
}
```

---

### ❌ Mistake 2: Not testing with "Don't Keep Activities"

```kotlin
// Code looks fine, but never tested process death
class MyViewModel : ViewModel() {
    val data = MutableLiveData<String>()
}

// Deployed to production...
// Users complain: "App loses my data!"
// Because you never tested process death!
```

### ✅ Correct way

```kotlin
// 1. Enable "Don't Keep Activities" in Developer Options
// 2. Add items to cart
// 3. Press Home button
// 4. Reopen app
// 5. Items should still be there!

// If items are gone → You need SavedStateHandle
```

---

### ❌ Mistake 3: Storing complex objects in SavedStateHandle

```kotlin
data class User(val id: String, val name: String, val settings: Settings)
data class Settings(/* complex nested data */)

// Wrong - SavedStateHandle can't serialize complex objects
class UserViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val user: MutableLiveData<User> =
        savedStateHandle.getLiveData("user")  // 💥 Won't work!
}
```

### ✅ Correct way

```kotlin
// Solution 1: Store simple values
class UserViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val userId: MutableLiveData<String> =
        savedStateHandle.getLiveData("user_id", "")
    val userName: MutableLiveData<String> =
        savedStateHandle.getLiveData("user_name", "")
}

// Solution 2: Make object Parcelable
@Parcelize
data class User(val id: String, val name: String) : Parcelable

class UserViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val user: MutableLiveData<User> =
        savedStateHandle.getLiveData("user")  // ✅ Works if Parcelable
}

// Solution 3: Use Room database for complex data
```

---

### ❌ Mistake 4: Forgetting to observe LiveData

```kotlin
class CartFragment : Fragment() {
    private val viewModel: CartViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Wrong - never observes, UI never updates!
        val items = viewModel.cartItems.value
        tvItems.text = items.toString()
    }
}
```

### ✅ Correct way

```kotlin
class CartFragment : Fragment() {
    private val viewModel: CartViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Right - observes LiveData, UI updates automatically
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            tvItems.text = items.toString()
        }
    }
}
```

---

### ❌ Mistake 5: Saving large data in SavedStateHandle

```kotlin
// Wrong - Bundle has size limit (~1MB)
class ImageViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val images: MutableLiveData<List<Bitmap>> =
        savedStateHandle.getLiveData("images", emptyList())
    // 💥 TransactionTooLargeException!
}
```

### ✅ Correct way

```kotlin
// Right - store references, not actual data
class ImageViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    // Store image URIs (small strings)
    val imageUris: MutableLiveData<ArrayList<String>> =
        savedStateHandle.getLiveData("image_uris", arrayListOf())
    
    // Load actual bitmaps when needed
    fun loadBitmap(uri: String): Bitmap {
        // Load from disk/network
    }
}
```

---

## 📊 Data Persistence Comparison

| Storage | Survives Rotation | Survives Process Death | Survives App Exit | Size Limit |
|---------|-------------------|------------------------|-------------------|------------|
| ViewModel (basic) | ✅ | ❌ | ❌ | Unlimited |
| SavedStateHandle | ✅ | ✅ | ❌ | ~1MB |
| onSaveInstanceState | ✅ | ✅ | ❌ | ~1MB |
| SharedPreferences | ✅ | ✅ | ✅ | ~10MB |
| Room Database | ✅ | ✅ | ✅ | Unlimited |
| DataStore | ✅ | ✅ | ✅ | ~10MB |

---

## 🧪 Testing Process Death

### Method 1: Don't Keep Activities (Easiest)

```
1. Settings → Developer Options → Enable "Don't Keep Activities"
2. Open app → Add data
3. Press Home button (app goes to background)
4. Reopen app
5. Check if data survived
```

### Method 2: ADB Command (Precise)

```bash
# Get your app's process ID
adb shell ps | grep com.yourapp

# Kill the process
adb shell am kill com.yourapp

# Reopen app from Recent Apps
```

### Method 3: Logcat Monitoring

```kotlin
// Add logging to detect restoration
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    if (savedInstanceState != null) {
        Log.d("ProcessDeath", "🔄 APP WAS KILLED AND RESTORED")
    } else {
        Log.d("ProcessDeath", "🆕 Fresh app launch")
    }
}
```

---

## 🎯 Mini Task

**What we're building:**  
A shopping cart that survives screen rotation AND process death using SavedStateHandle, with toast notifications when data is restored.

**What you'll learn:**
- How to use SavedStateHandle in ViewModel
- Why ArrayList works but MutableList doesn't
- How to test process death with "Don't Keep Activities"
- When to use SavedStateHandle vs Room database
- Production pattern for bulletproof data persistence
- How to show user-friendly restoration messages

---

## 📚 Quick Recap

- **SavedStateHandle** in ViewModel survives both rotation AND process death
- Use **ArrayList**, not MutableList (Bundle compatibility)
- **`getLiveData(key, default)`** returns auto-saving MutableLiveData
- Test with **"Don't Keep Activities"** developer option
- **onSaveInstanceState** for manual save (Activity/Fragment level)
- SavedStateHandle has **~1MB limit** (use Room for large data)
- ViewModel's **onCleared()** called on user exit, NOT process death
- Show **toast** when data is restored (better UX)
- **Production apps MUST handle process death** - it's tested in interviews

**Next Topic:** Services & WorkManager (background tasks that run even when app is closed)