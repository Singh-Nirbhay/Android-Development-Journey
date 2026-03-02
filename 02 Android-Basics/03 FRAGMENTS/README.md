# Fragments

> Reusable, modular UI components that live inside Activities - the building blocks of modern Android apps

---

## 🤔 What is it?

**Fragments** are mini-Activities that represent a portion of the UI. They have their own lifecycle, handle their own input, and can be added/removed while the Activity is running. Think of them as LEGO blocks - combine them to build complex screens.

An Activity is like a picture frame, Fragments are the paintings you swap in and out. You can have multiple Fragments in one Activity, navigate between them, and reuse them across different screens.

---

## 💡 Why do we need it in Android?

Modern apps have complex navigation: tabs, bottom navigation, multi-pane layouts (tablet). Managing everything in one Activity becomes a nightmare. Fragments solve this by letting you modularize your UI.

```kotlin
// Without Fragments - one giant Activity 😫
class MainActivity : ComponentActivity() {
    private var currentScreen = "home"
    
    setContent {
        when (currentScreen) {
            "home" -> HomeScreen(onNavigate = { currentScreen = "profile" })
            "profile" -> ProfileScreen(onBack = { currentScreen = "home" })
            "settings" -> SettingsScreen()
            // Becomes unmaintainable with 10+ screens!
        }
    }
}

// With Fragments - modular and maintainable 🎉
// Each screen is its own Fragment with lifecycle
// Navigation handles back stack automatically
// Reuse ProfileFragment in multiple Activities
```

Every production Android app uses Fragments for navigation, tabs, and complex layouts.

---

## 📌 Key Concepts

- **Fragment**: A modular UI component with its own lifecycle
- **FragmentManager**: Manages Fragment transactions (add, replace, remove)
- **FragmentTransaction**: A set of Fragment operations (add, replace, commit)
- **BackStack**: History of Fragment transactions (enables back navigation)
- **Arguments (Bundle)**: Pass data to Fragments safely
- **Factory Pattern**: Use `newInstance()` to create Fragments with arguments
- **Fragment Lifecycle**: Similar to Activity but with extra view-related callbacks
- **Container**: A layout (FrameLayout, FragmentContainerView) that holds Fragments

---

## ✍️ Fragment Lifecycle (Extended)

```
onCreate → onCreateView → onViewCreated → onStart → onResume
                                                         ↓
                                                      (Running)
                                                         ↓
onPause → onStop → onDestroyView → onDestroy → onDetach

Extra callbacks compared to Activity:
- onAttach: Fragment attached to Activity
- onCreateView: Create the view hierarchy
- onViewCreated: View is created (safe to reference views)
- onDestroyView: View is being destroyed (cleanup view references)
- onDetach: Fragment detached from Activity
```

---

## ✍️ Syntax

```kotlin
// ============================================
// PART 1: CREATE FRAGMENTS
// ============================================

// HomeFragment.kt
class HomeFragment : Fragment() {
    
    companion object {
        private const val TAG = "FragmentLifecycle"
        
        // Factory method (best practice)
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
    
    // ============================================
    // LIFECYCLE: onCreate
    // ============================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "HomeFragment - onCreate")
    }
    
    // ============================================
    // LIFECYCLE: onCreateView (Create UI)
    // ============================================
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "HomeFragment - onCreateView")
        
        // Inflate XML layout
        return inflater.inflate(R.layout.fragment_home, container, false)
        
        // OR use Compose (modern approach)
        // return ComposeView(requireContext()).apply {
        //     setContent {
        //         HomeScreen()
        //     }
        // }
    }
    
    // ============================================
    // LIFECYCLE: onViewCreated (Setup UI)
    // ============================================
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "HomeFragment - onViewCreated")
        
        // Find views and set listeners
        val btnGoToProfile: Button = view.findViewById(R.id.btnGoToProfile)
        
        btnGoToProfile.setOnClickListener {
            // Navigate to ProfileFragment
            val profileFragment = ProfileFragment.newInstance("Nirbhay")
            
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, profileFragment)
                .addToBackStack("profile")  // Enables back navigation
                .commit()
        }
    }
    
    // ============================================
    // LIFECYCLE: onDestroyView
    // ============================================
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "HomeFragment - onDestroyView")
        
        // Clean up view references to avoid memory leaks
    }
}

// ============================================
// ProfileFragment.kt
// ============================================
class ProfileFragment : Fragment() {
    
    companion object {
        private const val TAG = "FragmentLifecycle"
        private const val ARG_USER_NAME = "user_name"
        
        // Factory method with arguments
        fun newInstance(userName: String): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_NAME, userName)
                }
            }
        }
    }
    
    // Get argument safely
    private val userName: String by lazy {
        arguments?.getString(ARG_USER_NAME) ?: "Guest"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ProfileFragment - onCreate")
        Log.d(TAG, "User name: $userName")
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "ProfileFragment - onCreateView")
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ProfileFragment - onViewCreated")
        
        // Display user name
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        tvUserName.text = "Welcome, $userName!"
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "ProfileFragment - onDestroyView")
    }
}

// ============================================
// PART 2: MAIN ACTIVITY
// ============================================
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Load initial Fragment (only if savedInstanceState is null)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, HomeFragment.newInstance())
                .commit()
        }
    }
    
    // Optional: Handle back press
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}

// ============================================
// PART 3: LAYOUTS
// ============================================

// activity_main.xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragmentContainer"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />

// fragment_home.xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="16dp">
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Home Screen"
        android:textSize="24sp"
        android:textStyle="bold" />
    
    <Button
        android:id="@+id/btnGoToProfile"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Go to Profile" />
    
</LinearLayout>

// fragment_profile.xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="16dp">
    
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Profile Screen"
        android:textSize="24sp"
        android:textStyle="bold" />
    
    <TextView
        android:id="@+id/tvUserName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:textSize="18sp" />
    
</LinearLayout>
```

---

## 🎨 Modern Approach: Fragments with Compose

```kotlin
// HomeFragment with Jetpack Compose
class HomeFragment : Fragment() {
    
    companion object {
        fun newInstance() = HomeFragment()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    HomeScreen(
                        onNavigateToProfile = {
                            navigateToProfile("Nirbhay")
                        }
                    )
                }
            }
        }
    }
    
    private fun navigateToProfile(userName: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ProfileFragment.newInstance(userName))
            .addToBackStack("profile")
            .commit()
    }
}

@Composable
fun HomeScreen(onNavigateToProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Home Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onNavigateToProfile) {
            Text("Go to Profile")
        }
    }
}

// ProfileFragment with Compose
class ProfileFragment : Fragment() {
    
    companion object {
        private const val ARG_USER_NAME = "user_name"
        
        fun newInstance(userName: String) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_NAME, userName)
            }
        }
    }
    
    private val userName: String by lazy {
        arguments?.getString(ARG_USER_NAME) ?: "Guest"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ProfileScreen(userName = userName)
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(userName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Welcome, $userName!",
            style = MaterialTheme.typography.titleLarge
        )
    }
}
```

---

## ⚠️ Common Mistakes

### ❌ Mistake 1: Using Fragment constructor with parameters

```kotlin
// Wrong - arguments lost on configuration change!
class ProfileFragment(private val userName: String) : Fragment() {
    // userName will be null after screen rotation!
}

val fragment = ProfileFragment("John")  // ❌ Don't do this
```

### ✅ Correct way

```kotlin
// Right - use Bundle arguments
class ProfileFragment : Fragment() {
    companion object {
        private const val ARG_USER_NAME = "user_name"
        
        fun newInstance(userName: String) = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_NAME, userName)
            }
        }
    }
    
    private val userName: String
        get() = arguments?.getString(ARG_USER_NAME) ?: "Guest"
}
```

---

### ❌ Mistake 2: Forgetting `addToBackStack()`

```kotlin
// Wrong - back button exits app instead of going back
supportFragmentManager.beginTransaction()
    .replace(R.id.container, ProfileFragment.newInstance("John"))
    .commit()  // No back stack!
```

### ✅ Correct way

```kotlin
// Right - back button returns to previous Fragment
supportFragmentManager.beginTransaction()
    .replace(R.id.container, ProfileFragment.newInstance("John"))
    .addToBackStack("profile")  // ✅ Enables back navigation
    .commit()
```

---

### ❌ Mistake 3: Accessing views in `onCreateView`

```kotlin
override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    val view = inflater.inflate(R.layout.fragment_home, container, false)
    
    // Wrong - views might not be ready yet!
    val button: Button = view.findViewById(R.id.btnSubmit)
    button.setOnClickListener { }  // ⚠️ Can cause issues
    
    return view
}
```

### ✅ Correct way

```kotlin
override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
): View {
    return inflater.inflate(R.layout.fragment_home, container, false)
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    // Right - views are guaranteed to be ready
    val button: Button = view.findViewById(R.id.btnSubmit)
    button.setOnClickListener { }  // ✅ Safe
}
```

---

### ❌ Mistake 4: Using `getActivity()` without null check

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    // Wrong - activity might be null!
    val context = activity  // Can be null!
    Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()  // 💥 Crash
}
```

### ✅ Correct way

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    // Right - use requireActivity() or requireContext()
    Toast.makeText(requireContext(), "Hello", Toast.LENGTH_SHORT).show()
    
    // Or with null check
    activity?.let {
        Toast.makeText(it, "Hello", Toast.LENGTH_SHORT).show()
    }
}
```

---

### ❌ Mistake 5: Leaking views in Fragment

```kotlin
class HomeFragment : Fragment() {
    private var button: Button? = null  // ⚠️ Can leak memory
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button = view.findViewById(R.id.btnSubmit)
    }
    
    // onDestroyView called, but button still references destroyed view!
}
```

### ✅ Correct way

```kotlin
class HomeFragment : Fragment() {
    private var _button: Button? = null
    private val button get() = _button!!
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _button = view.findViewById(R.id.btnSubmit)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _button = null  // ✅ Clear reference
    }
}

// Better: Use View Binding (modern approach)
```

---

## 📊 Fragment vs Activity

| Feature | Activity | Fragment |
|---------|----------|----------|
| Lifecycle | Independent | Depends on Activity |
| Back Stack | System manages | FragmentManager manages |
| Reusability | Limited | High (use in multiple Activities) |
| UI Modularity | Low | High (combine multiple Fragments) |
| Arguments | Intent extras | Bundle arguments |

---

## 🎯 Mini Task

**What we're building:**  
A two-screen app with Fragment navigation - HomeFragment with a button that navigates to ProfileFragment, passing user data, with full back stack support.

**What you'll learn:**
- How to create Fragments with lifecycle logging
- How to use FragmentManager and FragmentTransaction
- How to pass data between Fragments using Bundle arguments
- How to use factory pattern (`newInstance()`) for Fragments
- How `addToBackStack()` enables back navigation
- Difference between `add()` and `replace()`
- When to use `onCreateView` vs `onViewCreated`

---

## 📚 Quick Recap

- **Fragments** are modular UI components with their own lifecycle
- Use **`newInstance()` factory pattern** to create Fragments with arguments
- Pass data via **Bundle arguments** (survives configuration changes)
- **FragmentManager** handles transactions: `add()`, `replace()`, `remove()`
- **`addToBackStack()`** enables back navigation between Fragments
- **`onCreateView`** creates the view, **`onViewCreated`** sets up the view
- Use **`requireContext()`** and **`requireActivity()`** instead of nullable getters
- Clean up view references in **`onDestroyView`** to avoid memory leaks
- Modern apps use **Compose in Fragments** for UI (hybrid approach)

**Next Topic:** Navigation Component (modern, type-safe Fragment navigation with deep links)