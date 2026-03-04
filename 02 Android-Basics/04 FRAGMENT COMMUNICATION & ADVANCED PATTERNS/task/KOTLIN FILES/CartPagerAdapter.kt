package com.example.quickcart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class CartPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Total number of tabs
    override fun getItemCount(): Int = 3

    // Which fragment for which position
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CartFragment()
            1 -> SummaryFragment()
            2 -> ProfileFragment.newInstance("Nirbhay Singh") // Bundle pattern!
            else -> CartFragment()
        }
    }
}