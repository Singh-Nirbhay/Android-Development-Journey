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

    // SAME ViewModel instance as CartFragment!
    // Because activityViewModels() scopes to Activity
    private val viewModel: SharedCartViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "SummaryFragment: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SummaryFragment: onCreate")
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

        // Observe item count — SAME data as CartFragment sees!
        viewModel.itemCount.observe(viewLifecycleOwner) { count ->
            tvTotalItems.text = "Total Items: $count"
        }

        // Observe cart items for summary text
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