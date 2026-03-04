package com.example.quickcart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class AddItemDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "FragmentLifecycle"
    }

    // Using onCreateView (Approach 2) — Custom XML layout
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "AddItemDialog: onCreateView")
        return inflater.inflate(R.layout.dialog_add_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "AddItemDialog: onViewCreated")

        val etItemName = view.findViewById<EditText>(R.id.et_item_name)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        btnSave.setOnClickListener {
            val itemName = etItemName.text.toString().trim()

            // Validation — don't accept empty input
            if (itemName.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Enter a name!",
                    Toast.LENGTH_SHORT
                ).show()
                // return@setOnClickListener = return from LAMBDA only
                // Without label, return would exit onViewCreated!
                return@setOnClickListener
            }

            // Package result in Bundle
            val bundle = Bundle()
            bundle.putString("item_name", itemName)

            // Send result BACK to whoever is listening
            // parentFragmentManager = CartFragment's childFragmentManager
            // (because CartFragment used childFragmentManager to show this)
            parentFragmentManager.setFragmentResult(
                "add_item_request",  // Must match listener's key!
                bundle
            )

            // Close dialog
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "AddItemDialog: onDestroyView")
    }
}