package com.example.quickcart

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

class CartFragment : Fragment() {

    companion object {
        private const val TAG = "FragmentLifecycle"
    }

    // Shared ViewModel — scoped to ACTIVITY (not this fragment)
    // All fragments get SAME instance
    private val viewModel: SharedCartViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "CartFragment: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "CartFragment: onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "CartFragment: onCreateView")
        // ONLY inflate and return. Nothing else here!
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "CartFragment: onViewCreated")

        // Find all views
        val tvCartItems = view.findViewById<TextView>(R.id.tv_cart_items)
        val tvItemCount = view.findViewById<TextView>(R.id.tv_item_count)
        val btnAddItem = view.findViewById<Button>(R.id.btn_add_item)
        val btnClearCart = view.findViewById<Button>(R.id.btn_clear_cart)

        // ━━━ OBSERVE LiveData from ViewModel ━━━

        // Observe cart items — updates whenever list changes
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

        // ━━━ FRAGMENT RESULT LISTENERS ━━━
        // Must be set BEFORE opening dialogs
        // Using childFragmentManager because dialogs are CHILDREN of this fragment

        // Listen for result from AddItemDialog
        childFragmentManager.setFragmentResultListener(
            "add_item_request",    // Must match sender's key
            viewLifecycleOwner     // Auto-cleanup when view destroyed
        ) { _, bundle ->
            val itemName = bundle.getString("item_name", "")
            if (itemName.isNotEmpty()) {
                viewModel.addItem(itemName)
            }
        }

        // Listen for result from ClearCartDialog
        childFragmentManager.setFragmentResultListener(
            "clear_cart_request",  // Must match sender's key
            viewLifecycleOwner
        ) { _, bundle ->
            val confirmed = bundle.getBoolean("confirmed", false)
            if (confirmed) {
                viewModel.clearCart()
            }
        }

        // ━━━ BUTTON CLICK LISTENERS ━━━

        // Open Add Item dialog
        btnAddItem.setOnClickListener {
            val dialog = AddItemDialogFragment()
            // Using childFragmentManager — dialog becomes CHILD of CartFragment
            dialog.show(childFragmentManager, "AddItemDialog")
        }

        // Open Clear Cart confirmation dialog
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