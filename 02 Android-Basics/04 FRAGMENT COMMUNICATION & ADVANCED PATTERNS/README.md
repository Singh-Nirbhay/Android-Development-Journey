# Fragment Communication & Advanced Patterns

> Master inter-fragment communication, dialogs, tabs, and modern single-activity architecture

---

## 🎯 What You'll Learn

This comprehensive guide covers how Fragments talk to each other, reusable dialog patterns, swipeable tab layouts, and the modern single-activity architecture that powers production apps.

---

## 📑 Table of Contents

- [Part 1: Fragment Communication](#part-1-fragment-communication)
    - [Shared ViewModel](#solution-1-shared-viewmodel-modern)
    - [Fragment Result API](#solution-2-fragment-result-api-official)
- [Part 2: DialogFragment](#part-2-dialogfragment)
- [Part 3: ViewPager2 + Tabs](#part-3-viewpager2--tabs)
- [Part 4: Single Activity Architecture](#part-4-single-activity-architecture)
- [Grand Finale Challenge](#-build-challenge-3b)

---

## PART 1: Fragment Communication

### 🤔 The Problem

Two Fragments need to communicate, but they shouldn't know about each other directly (tight coupling is bad). How do they share data?

```kotlin
// ❌ BAD: Direct coupling
class FragmentA : Fragment() {
    fun sendData() {
        val fragmentB = parentFragmentManager.findFragmentByTag("B") as FragmentB
        fragmentB.updateUI("Hello")  // Tightly coupled, fragile
    }
}
```

### ✅ The Solutions

---

## Solution 1: Shared ViewModel (Modern)

**Best for:** Sharing data between Fragments in the same Activity, reactive updates

```kotlin
// ============================================
// SHARED VIEWMODEL
// ============================================
class SharedCartViewModel : ViewModel() {
    private val _items = MutableLiveData<List<String>>(emptyList())
    val items: LiveData<List<String>> = _items
    
    private val _lastAddedItem = MutableLiveData<String>()
    val lastAddedItem: LiveData<String> = _lastAddedItem
    
    fun addItem(itemName: String) {
        val currentList = _items.value.orEmpty().toMutableList()
        currentList.add(itemName)
        _items.value = currentList
        _lastAddedItem.value = itemName
    }
}

// ============================================
// FRAGMENT A (Producer)
// ============================================
class AddItemFragment : Fragment() {
    
    // Shared ViewModel scoped to Activity
    private val sharedViewModel: SharedCartViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AddItemScreen(
                    onAddItem = { itemName ->
                        sharedViewModel.addItem(itemName)
                    }
                )
            }
        }
    }
}

@Composable
fun AddItemScreen(onAddItem: (String) -> Unit) {
    var itemName by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = itemName,
            onValueChange = { itemName = it },
            label = { Text("Item Name") }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = {
            if (itemName.isNotBlank()) {
                onAddItem(itemName)
                itemName = ""
            }
        }) {
            Text("Add Item")
        }
    }
}

// ============================================
// FRAGMENT B (Consumer)
// ============================================
class CartListFragment : Fragment() {
    
    // Same ViewModel instance (shared!)
    private val sharedViewModel: SharedCartViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val items by sharedViewModel.items.observeAsState(emptyList())
                val lastAdded by sharedViewModel.lastAddedItem.observeAsState("")
                
                CartListScreen(
                    items = items,
                    lastAddedItem = lastAdded
                )
            }
        }
    }
}

@Composable
fun CartListScreen(items: List<String>, lastAddedItem: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Shopping Cart",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (lastAddedItem.isNotBlank()) {
            Text(
                text = "Last added: $lastAddedItem",
                color = Color.Green,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(items) { item ->
                Text(
                    text = "• $item",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
```

---

## Solution 2: Fragment Result API (Official)

**Best for:** One-time results (like dialogs), no dependencies on ViewModel

```kotlin
// ============================================
// SENDER FRAGMENT (Listener)
// ============================================
class ListFragment : Fragment() {
    
    companion object {
        private const val REQUEST_KEY = "add_item_request"
        const val ITEM_NAME_KEY = "item_name"
    }
    
    private var lastAddedItem by mutableStateOf("")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set result listener BEFORE showing dialog
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY,
            this  // LifecycleOwner
        ) { requestKey, bundle ->
            // Called when dialog sends result
            val itemName = bundle.getString(ITEM_NAME_KEY, "")
            lastAddedItem = itemName
            Log.d("ListFragment", "Received item: $itemName")
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ListScreen(
                    lastAddedItem = lastAddedItem,
                    onAddItemClick = {
                        showAddItemDialog()
                    }
                )
            }
        }
    }
    
    private fun showAddItemDialog() {
        AddItemDialogFragment.newInstance()
            .show(parentFragmentManager, "AddItemDialog")
    }
}

@Composable
fun ListScreen(lastAddedItem: String, onAddItemClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Shopping List",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (lastAddedItem.isNotBlank()) {
            Text(
                text = "Last added: $lastAddedItem",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF4CAF50)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onAddItemClick) {
            Text("Add Item")
        }
    }
}

// ============================================
// RECEIVER FRAGMENT (Dialog)
// ============================================
class AddItemDialogFragment : DialogFragment() {
    
    companion object {
        private const val REQUEST_KEY = "add_item_request"
        const val ITEM_NAME_KEY = "item_name"
        
        fun newInstance() = AddItemDialogFragment()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AddItemDialogContent(
                    onSave = { itemName ->
                        sendResult(itemName)
                        dismiss()
                    },
                    onCancel = { dismiss() }
                )
            }
        }
    }
    
    private fun sendResult(itemName: String) {
        val result = Bundle().apply {
            putString(ITEM_NAME_KEY, itemName)
        }
        
        // Send result back to listener
        parentFragmentManager.setFragmentResult(REQUEST_KEY, result)
    }
}

@Composable
fun AddItemDialogContent(
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Add Item",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = {
                        if (itemName.isNotBlank()) {
                            onSave(itemName)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        }
    }
}
```

---

## PART 2: DialogFragment

### 🤔 What is it?

**DialogFragment** is a Fragment that displays a dialog window. Better than `AlertDialog` because it survives configuration changes and integrates with Fragment lifecycle.

```kotlin
// ============================================
// SIMPLE DIALOG FRAGMENT
// ============================================
class ConfirmDialogFragment : DialogFragment() {
    
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"
        const val REQUEST_KEY = "confirm_dialog"
        const val RESULT_KEY = "confirmed"
        
        fun newInstance(title: String, message: String): ConfirmDialogFragment {
            return ConfirmDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                }
            }
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(ARG_TITLE) ?: "Confirm"
        val message = arguments?.getString(ARG_MESSAGE) ?: ""
        
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                sendResult(true)
            }
            .setNegativeButton("No") { _, _ ->
                sendResult(false)
            }
            .create()
    }
    
    private fun sendResult(confirmed: Boolean) {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY,
            Bundle().apply { putBoolean(RESULT_KEY, confirmed) }
        )
    }
}

// Usage
class HomeFragment : Fragment() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        parentFragmentManager.setFragmentResultListener(
            ConfirmDialogFragment.REQUEST_KEY,
            this
        ) { _, bundle ->
            val confirmed = bundle.getBoolean(ConfirmDialogFragment.RESULT_KEY)
            if (confirmed) {
                performAction()
            }
        }
    }
    
    private fun showConfirmDialog() {
        ConfirmDialogFragment.newInstance(
            title = "Delete Item",
            message = "Are you sure?"
        ).show(parentFragmentManager, "confirm")
    }
}
```

---

## PART 3: ViewPager2 + Tabs

### 🤔 What is it?

**ViewPager2** lets users swipe between Fragments horizontally. **TabLayout** adds tabs at the top. Think Instagram stories or app onboarding.

```kotlin
// ============================================
// MAIN ACTIVITY WITH VIEWPAGER
// ============================================
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        
        // Set adapter
        viewPager.adapter = MainPagerAdapter(this)
        
        // Connect tabs with ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Home"
                1 -> "Search"
                2 -> "Profile"
                else -> "Tab $position"
            }
            tab.icon = when (position) {
                0 -> ContextCompat.getDrawable(this, R.drawable.ic_home)
                1 -> ContextCompat.getDrawable(this, R.drawable.ic_search)
                2 -> ContextCompat.getDrawable(this, R.drawable.ic_profile)
                else -> null
            }
        }.attach()
    }
}

// ============================================
// VIEWPAGER ADAPTER
// ============================================
class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    
    override fun getItemCount(): Int = 3
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment.newInstance()
            1 -> SearchFragment.newInstance()
            2 -> ProfileFragment.newInstance()
            else -> HomeFragment.newInstance()
        }
    }
}

// ============================================
// LAYOUT: activity_main.xml
// ============================================
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    
    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tabLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:tabMode="fixed"
        app:tabGravity="fill" />
    
    <androidx.viewpager2.widget.ViewPager2
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />
    
</LinearLayout>

// ============================================
// GRADLE DEPENDENCIES
// ============================================
dependencies {
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.google.android.material:material:1.9.0")
}
```

---

## PART 4: Single Activity Architecture

### 🤔 What is it?

**One Activity, many Fragments**. Navigation Component handles all transitions. This is the **modern Android standard** (used by Google, Instagram, Twitter).

```kotlin
// ============================================
// SETUP: build.gradle (app)
// ============================================
dependencies {
    val navVersion = "2.7.0"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-compose:$navVersion")
}

// ============================================
// NAVIGATION GRAPH: res/navigation/nav_graph.xml
// ============================================
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">
    
    <fragment
        android:id="@+id/homeFragment"
        android:name="com.example.app.HomeFragment"
        android:label="Home">
        <action
            android:id="@+id/action_home_to_profile"
            app:destination="@id/profileFragment" />
    </fragment>
    
    <fragment
        android:id="@+id/profileFragment"
        android:name="com.example.app.ProfileFragment"
        android:label="Profile">
        <argument
            android:name="userId"
            app:argType="string" />
    </fragment>
    
</navigation>

// ============================================
// MAIN ACTIVITY (Single Activity)
// ============================================
class MainActivity : AppCompatActivity() {
    
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        
        navController = navHostFragment.navController
        
        // Optional: Setup with ActionBar/Toolbar
        setupActionBarWithNavController(navController)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

// activity_main.xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />

// ============================================
// NAVIGATION IN FRAGMENTS
// ============================================
class HomeFragment : Fragment() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Navigate with Safe Args (type-safe!)
        view.findViewById<Button>(R.id.btnGoToProfile).setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToProfile(userId = "123")
            findNavController().navigate(action)
        }
    }
}

class ProfileFragment : Fragment() {
    
    private val args: ProfileFragmentArgs by navArgs()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val userId = args.userId
        Log.d("ProfileFragment", "User ID: $userId")
    }
}
```

---

## 🏗️ BUILD CHALLENGE #3B

**The Grand Finale:** Shopping Cart with Fragment Result API

### Requirements

```kotlin
// ============================================
// STEP 1: LIST FRAGMENT
// ============================================
class ShoppingListFragment : Fragment() {
    
    companion object {
        private const val REQUEST_KEY_ADD_ITEM = "request_add_item"
        const val KEY_ITEM_NAME = "item_name"
        
        fun newInstance() = ShoppingListFragment()
    }
    
    private var lastAddedItem by mutableStateOf("")
    private val items = mutableStateListOf<String>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Listen for result from dialog
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_ADD_ITEM,
            this
        ) { _, bundle ->
            val itemName = bundle.getString(KEY_ITEM_NAME, "")
            if (itemName.isNotBlank()) {
                items.add(itemName)
                lastAddedItem = itemName
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ShoppingListScreen(
                        items = items,
                        lastAddedItem = lastAddedItem,
                        onAddItemClick = {
                            showAddItemDialog()
                        }
                    )
                }
            }
        }
    }
    
    private fun showAddItemDialog() {
        AddItemDialogFragment.newInstance()
            .show(parentFragmentManager, "AddItemDialog")
    }
}

@Composable
fun ShoppingListScreen(
    items: List<String>,
    lastAddedItem: String,
    onAddItemClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Shopping Cart",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Last added item indicator
        if (lastAddedItem.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                )
            ) {
                Text(
                    text = "✅ Last added: $lastAddedItem",
                    modifier = Modifier.padding(12.dp),
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Add button
        Button(
            onClick = onAddItemClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Item")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Items list
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No items yet.\nClick 'Add Item' to start!",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn {
                itemsIndexed(items) { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}.",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(text = item)
                        }
                    }
                }
            }
        }
    }
}

// ============================================
// STEP 2: DIALOG FRAGMENT
// ============================================
class AddItemDialogFragment : DialogFragment() {
    
    companion object {
        private const val REQUEST_KEY = "request_add_item"
        const val KEY_ITEM_NAME = "item_name"
        
        fun newInstance() = AddItemDialogFragment()
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Make dialog full width
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    AddItemDialogScreen(
                        onSave = { itemName ->
                            sendResult(itemName)
                            dismiss()
                        },
                        onCancel = { dismiss() }
                    )
                }
            }
        }
    }
    
    private fun sendResult(itemName: String) {
        parentFragmentManager.setFragmentResult(
            REQUEST_KEY,
            Bundle().apply {
                putString(KEY_ITEM_NAME, itemName)
            }
        )
    }
}

@Composable
fun AddItemDialogScreen(
    onSave: (String) -> Unit,
    onCancel: () -> Unit
) {
    var itemName by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Add New Item",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                placeholder = { Text("e.g., Apple, Milk, Bread") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (itemName.isNotBlank()) {
                            onSave(itemName)
                        }
                    }
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = {
                        if (itemName.isNotBlank()) {
                            onSave(itemName)
                        }
                    },
                    enabled = itemName.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        }
    }
}

// ============================================
// STEP 3: MAIN ACTIVITY
// ============================================
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Load fragment
                    AndroidView(
                        factory = { context ->
                            FrameLayout(context).apply {
                                id = View.generateViewId()
                            }
                        },
                        update = { frameLayout ->
                            if (supportFragmentManager.findFragmentById(frameLayout.id) == null) {
                                supportFragmentManager.beginTransaction()
                                    .replace(
                                        frameLayout.id,
                                        ShoppingListFragment.newInstance()
                                    )
                                    .commit()
                            }
                        }
                    )
                }
            }
        }
    }
}
```

---

## 📚 Complete Recap

### Fragment Communication
- **Shared ViewModel**: Best for reactive data sharing between Fragments in same Activity
- **Fragment Result API**: Best for one-time results (dialogs, forms)
- Use `activityViewModels()` to share ViewModel across Fragments
- Set listener BEFORE showing dialog with `setFragmentResultListener`

### DialogFragment
- Better than AlertDialog - survives config changes
- Use `onCreateDialog()` for simple dialogs
- Use `onCreateView()` for complex custom UIs
- Send results back with Fragment Result API

### ViewPager2 + Tabs
- Swipeable Fragment container
- Use `FragmentStateAdapter` for adapter
- Connect tabs with `TabLayoutMediator`
- Common pattern: onboarding, image galleries, app sections

### Single Activity Architecture
- ONE Activity, many Fragments (modern standard)
- Navigation Component handles transitions
- Type-safe navigation with Safe Args
- Simpler lifecycle management

---

**Next Topic:** Services & Background Work (run code when app is closed, push notifications, download files)