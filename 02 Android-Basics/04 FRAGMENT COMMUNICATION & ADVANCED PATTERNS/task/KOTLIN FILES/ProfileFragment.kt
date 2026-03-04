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

        // Factory method — the SAFE way to create fragment with data
        fun newInstance(userName: String): ProfileFragment {
            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putString(KEY_USER_NAME, userName)
            fragment.arguments = bundle
            return fragment
        }
    }

    // Shared ViewModel — can see cart data from other tabs
    private val viewModel: SharedCartViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "ProfileFragment: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ProfileFragment: onCreate")
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

        // Display name from BUNDLE arguments (not ViewModel!)
        // This data was passed at creation time via newInstance()
        val userName = arguments?.getString(KEY_USER_NAME, "Unknown")
        tvWelcome.text = "Welcome, $userName!"

        // Display cart count from SHARED VIEWMODEL
        // Proves ViewModel works across ALL tabs
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