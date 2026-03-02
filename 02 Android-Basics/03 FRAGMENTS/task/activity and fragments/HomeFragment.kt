package com.focusguard.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class HomeFragment : Fragment() {
    companion object {
        const val TAG = "Understanding_Fragments"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "State -> onAttach, fragment -> HomeFragment")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "State -> onCreate, fragment -> HomeFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(
            TAG, "State -> onCreateView, fragment -> HomeFragment"
        )
        return inflater.inflate(R.layout.fragment_home, container, false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "State -> onViewCreated, fragment -> HomeFragment")
        val btnHomeToProfile = view.findViewById<Button>(R.id.btn_home_to_profile)
        btnHomeToProfile.setOnClickListener {
            val profileFragment = ProfileFragment.newInstance("Nirbhay Singh")
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, profileFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "State -> onStart , fragment -> HomeFragment" )

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "State -> onResume , fragment -> HomeFragment")

    }
    override fun onPause(){
        super.onPause()
        Log.d(TAG, "State -> onPause, fragment -> HomeFragment")
    }
    override fun onStop(){
        super.onStop()
        Log.d(TAG, "State -> onStop, fragment -> HomeFragment")
    }
    override fun onDestroyView(){
        super.onDestroyView()
        Log.d(TAG, "State -> onDestroyView, fragment -> HomeFragment")
    }
    override fun onDestroy(){
        super.onDestroy()
        Log.d(TAG, "State -> onDestroy, fragment -> HomeFragment")
    }
    override fun onDetach(){
        super.onDetach()
        Log.d(TAG, "State -> onDetach, fragment -> HomeFragment")
    }

}
