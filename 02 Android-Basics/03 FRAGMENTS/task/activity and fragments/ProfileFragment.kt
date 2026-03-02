package com.focusguard.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.focusguard.fragments.HomeFragment.Companion.TAG

class ProfileFragment : Fragment() {
    companion object{
       private const val KEY_USER_NAME = "KEY_USER_NAME"

        fun newInstance(name: String) : ProfileFragment{
            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putString(KEY_USER_NAME,name)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "State -> onAttach, fragment -> ProfileFragment")

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "State -> onCreate, fragment -> ProfileFragment")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        Log.d(
            TAG, "State -> onCreateView, fragment -> ProfileFragment"
        )

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view:View , savedInstanceState:Bundle?){
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "State -> onViewCreated, fragment -> ProfileFragment")
        val useName = arguments?.getString(KEY_USER_NAME , "Unknown")
        val tvUserName = view.findViewById<TextView>(R.id.tv_user_name)
        tvUserName.text = useName
       val btnProfileToHome = view.findViewById<Button>(R.id.btn_profile_to_home)
        btnProfileToHome.setOnClickListener{

            parentFragmentManager.popBackStack()


        }

    }



    override fun onStart() {
        super.onStart()
        Log.d(TAG, "State -> onStart , fragment -> ProfileFragment" )

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "State -> onResume , fragment -> ProfileFragment")

    }
    override fun onPause(){
        super.onPause()
        Log.d(TAG, "State -> onPause, fragment -> ProfileFragment")
    }
    override fun onStop(){
        super.onStop()
        Log.d(TAG, "State -> onStop, fragment -> ProfileFragment")
    }
    override fun onDestroyView(){
        super.onDestroyView()
        Log.d(TAG, "State -> onDestroyView, fragment -> ProfileFragment")
    }
    override fun onDestroy(){
        super.onDestroy()
        Log.d(TAG, "State -> onDestroy, fragment -> ProfileFragment")
    }
    override fun onDetach(){
        super.onDetach()
        Log.d(TAG, "State -> onDetach, fragment -> ProfileFragment")
    }

}