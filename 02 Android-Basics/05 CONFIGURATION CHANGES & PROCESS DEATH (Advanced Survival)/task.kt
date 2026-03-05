1. SharedCartViewModel.kt (MAJOR UPDATE)
Kotlin

package com.example.quickcart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SharedCartViewModel(
    private val savedStateHandle: SavedStateHandle  // Injected automatically!
) : ViewModel() {

    companion object {
        private const val TAG = "CartViewModel"
        // Keys for SavedStateHandle — like SharedPreferences keys
        private const val KEY_CART_ITEMS = "key_cart_items"
        private const val KEY_ITEM_COUNT = "key_item_count"
    }

    // ━━━ BEFORE (Lost on process death) ━━━
    // private val _cartItems = MutableLiveData<MutableList<String>>(mutableListOf())
    // private val _itemCount = MutableLiveData(0)

    // ━━━ AFTER (Survives process death!) ━━━
    // getLiveData() → Returns MutableLiveData that auto-saves to SavedStateHandle
    // First param = key (for identification)
    // Second param = default value (used when no saved state exists)

    // For list of strings → Use ArrayList<String> (Serializable, Bundle-compatible)
    val cartItems: MutableLiveData<ArrayList<String>> =
        savedStateHandle.getLiveData(KEY_CART_ITEMS, arrayListOf())

    val itemCount: MutableLiveData<Int> =
        savedStateHandle.getLiveData(KEY_ITEM_COUNT, 0)

    init {
        // Check if data was restored from process death
        val restoredItems = cartItems.value
        if (!restoredItems.isNullOrEmpty()) {
            Log.d(TAG, "✅ Data RESTORED from saved state! Items: $restoredItems")
        } else {
            Log.d(TAG, "🆕 Fresh start — no saved state found")
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
        Log.d(TAG, "➕ Added: $name | Total: ${currentList.size} | Saved to state ✅")
    }

    fun clearCart() {
        cartItems.value = arrayListOf()
        itemCount.value = 0
        Log.d(TAG, "🗑️ Cart cleared | Saved to state ✅")
    }

    fun getCartSummary(): String {
        val items = cartItems.value
        return if (items.isNullOrEmpty()) {
            "Cart is empty"
        } else {
            "Items: ${items.joinToString(", ")}"
        }
    }

    // Called when ViewModel is permanently destroyed (not process death)
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "❌ ViewModel onCleared — permanently destroyed")
    }
}
What Changed And WHY:
text

CHANGE 1: Constructor parameter
OLD: class SharedCartViewModel : ViewModel()
NEW: class SharedCartViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel()

WHY: SavedStateHandle is injected by AndroidX automatically
No factory needed! activityViewModels() handles it


CHANGE 2: MutableLiveData → savedStateHandle.getLiveData()
OLD: private val _cartItems = MutableLiveData<MutableList<String>>()
NEW: val cartItems: MutableLiveData<ArrayList<String>> =
    savedStateHandle.getLiveData(KEY_CART_ITEMS, arrayListOf())

WHY: getLiveData() returns a LiveData that auto-saves!
Every time .value changes → saved to Bundle
On recreation → restored from Bundle


CHANGE 3: MutableList → ArrayList
OLD: MutableList<String>
NEW: ArrayList<String>

WHY: SavedStateHandle stores data in a Bundle
Bundle can store ArrayList (Serializable) ✅
Bundle CANNOT store MutableList interface ❌
ArrayList implements both MutableList AND Serializable


CHANGE 4: No more private backing field pattern
OLD: private val _cartItems + val cartItems (two variables)
NEW: val cartItems (one variable, MutableLiveData)

WHY: getLiveData() returns MutableLiveData directly
We COULD still wrap it in private/public pattern
But for this learning exercise, keeping it simple

In production, you would do:
private val _cartItems = savedStateHandle.getLiveData(...)
val cartItems: LiveData<ArrayList<String>> = _cartItems


CHANGE 5: Added init block for logging
WHY: To SEE in Logcat whether data was restored or fresh
This proves SavedStateHandle works!


CHANGE 6: Added onCleared()
WHY: To understand when ViewModel is TRULY destroyed
onCleared() is called when Activity FINISHES (user exits)
NOT called on process death (data saved before that)
2. CartFragment.kt (Small Update — Logging Only)
Kotlin

package com.example.quickcart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class CartFragment : Fragment() {

    companion object {
        private const val TAG = "FragmentLifecycle"
    }

    private val viewModel: SharedCartViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "CartFragment: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ━━━ NEW: Log whether this is fresh or restored ━━━
        if (savedInstanceState != null) {
            Log.d(TAG, "CartFragment: onCreate — 🔄 RESTORED from saved state")
        } else {
            Log.d(TAG, "CartFragment: onCreate — 🆕 Fresh creation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "CartFragment: onCreateView")
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "CartFragment: onViewCreated")

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

        // ━━━ NEW: Show toast if cart was restored ━━━
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

        // Fragment Result Listeners
        childFragmentManager.setFragmentResultListener(
            "add_item_request",
            viewLifecycleOwner
        ) { _, bundle ->
            val itemName = bundle.getString("item_name", "")
            if (itemName.isNotEmpty()) {
                viewModel.addItem(itemName)
            }
        }

        childFragmentManager.setFragmentResultListener(
            "clear_cart_request",
            viewLifecycleOwner
        ) { _, bundle ->
            val confirmed = bundle.getBoolean("confirmed", false)
            if (confirmed) {
                viewModel.clearCart()
            }
        }

        // Button clicks
        btnAddItem.setOnClickListener {
            val dialog = AddItemDialogFragment()
            dialog.show(childFragmentManager, "AddItemDialog")
        }

        btnClearCart.setOnClickListener {
            val dialog = ClearCartDialogFragment()
            dialog.show(childFragmentManager, "ClearCartDialog")
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "CartFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "CartFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "CartFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "CartFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "CartFragment: onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "CartFragment: onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "CartFragment: onDetach")
    }
}
3. SummaryFragment.kt (Small Update — Logging Only)
Kotlin

package com.example.quickcart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class SummaryFragment : Fragment() {

    companion object {
        private const val TAG = "FragmentLifecycle"
    }

    private val viewModel: SharedCartViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "SummaryFragment: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ━━━ NEW: Log restoration status ━━━
        if (savedInstanceState != null) {
            Log.d(TAG, "SummaryFragment: onCreate — 🔄 RESTORED from saved state")
        } else {
            Log.d(TAG, "SummaryFragment: onCreate — 🆕 Fresh creation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "SummaryFragment: onCreateView")
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "SummaryFragment: onViewCreated")

        val tvTotalItems = view.findViewById<TextView>(R.id.tv_total_items)
        val tvSummary = view.findViewById<TextView>(R.id.tv_summary)

        viewModel.itemCount.observe(viewLifecycleOwner) { count ->
            tvTotalItems.text = "Total Items: $count"
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            tvSummary.text = if (items.isNullOrEmpty()) {
                "Cart is empty"
            } else {
                "Items: ${items.joinToString(", ")}"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "SummaryFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "SummaryFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "SummaryFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "SummaryFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "SummaryFragment: onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SummaryFragment: onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "SummaryFragment: onDetach")
    }
}
4. ProfileFragment.kt (Small Update — Logging Only)
Kotlin

package com.example.quickcart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class ProfileFragment : Fragment() {

    companion object {
        private const val TAG = "FragmentLifecycle"
        private const val KEY_USER_NAME = "key_user_name"

        fun newInstance(userName: String): ProfileFragment {
            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putString(KEY_USER_NAME, userName)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val viewModel: SharedCartViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "ProfileFragment: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ━━━ NEW: Log restoration status ━━━
        if (savedInstanceState != null) {
            Log.d(TAG, "ProfileFragment: onCreate — 🔄 RESTORED from saved state")
        } else {
            Log.d(TAG, "ProfileFragment: onCreate — 🆕 Fresh creation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "ProfileFragment: onCreateView")
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "ProfileFragment: onViewCreated")

        val tvWelcome = view.findViewById<TextView>(R.id.tv_welcome)
        val tvCartCount = view.findViewById<TextView>(R.id.tv_cart_count)

        val userName = arguments?.getString(KEY_USER_NAME, "Unknown")
        tvWelcome.text = "Welcome, $userName!"

        viewModel.itemCount.observe(viewLifecycleOwner) { count ->
            tvCartCount.text = "You have $count items in cart"
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "ProfileFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "ProfileFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "ProfileFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "ProfileFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "ProfileFragment: onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ProfileFragment: onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "ProfileFragment: onDetach")
    }
}