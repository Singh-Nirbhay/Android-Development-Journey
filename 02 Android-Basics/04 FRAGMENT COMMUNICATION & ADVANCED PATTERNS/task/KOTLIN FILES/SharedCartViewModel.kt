package com.example.quickcart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedCartViewModel : ViewModel() {

    // Private mutable — only ViewModel can modify
    private val _cartItems = MutableLiveData<MutableList<String>>(mutableListOf())
    // Public read-only — fragments can only observe
    val cartItems: LiveData<MutableList<String>> = _cartItems

    private val _itemCount = MutableLiveData(0)
    val itemCount: LiveData<Int> = _itemCount

    fun addItem(name: String) {
        // Get current list (or create empty one)
        val currentList = _cartItems.value ?: mutableListOf()
        // Add new item
        currentList.add(name)
        // RE-SET value to trigger LiveData notification!
        // Just adding to list does NOT notify observers
        _cartItems.value = currentList
        _itemCount.value = currentList.size
    }

    fun clearCart() {
        // Set brand new empty list
        _cartItems.value = mutableListOf()
        _itemCount.value = 0
    }

    fun getCartSummary(): String {
        val items = _cartItems.value
        return if (items.isNullOrEmpty()) {
            "Cart is empty"
        } else {
            "Items: ${items.joinToString(", ")}"
        }
    }
}