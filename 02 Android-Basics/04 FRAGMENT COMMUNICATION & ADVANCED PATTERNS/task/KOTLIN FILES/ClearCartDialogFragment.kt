package com.example.quickcart

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ClearCartDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "FragmentLifecycle"
    }

    // Using onCreateDialog (Approach 1) — AlertDialog.Builder
    // No custom XML needed!
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d(TAG, "ClearCartDialog: onCreateDialog")

        return AlertDialog.Builder(requireContext())
            .setTitle("Clear Cart?")
            .setMessage("This will remove all items. Are you sure?")
            .setPositiveButton("Yes, Clear") { _, _ ->
                // Package confirmation in Bundle
                val bundle = Bundle()
                bundle.putBoolean("confirmed", true)

                // Send result back
                parentFragmentManager.setFragmentResult(
                    "clear_cart_request",  // Must match listener's key!
                    bundle
                )
                // AlertDialog auto-dismisses on button click
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Just close — AlertDialog auto-dismisses
                dismiss()
            }
            .create() // create() NOT show()!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "ClearCartDialog: onDestroyView")
    }
}