package com.example.arfurnitureapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.arfurnitureapp.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesViewModel : ViewModel() {
    // List of saved product IDs
    private val _savedProductIds = MutableStateFlow<Set<String>>(emptySet())
    val savedProductIds: StateFlow<Set<String>> = _savedProductIds.asStateFlow()

    // List of saved products
    private val _savedProducts = MutableStateFlow<List<Product>>(emptyList())
    val savedProducts: StateFlow<List<Product>> = _savedProducts.asStateFlow()

    // Toggle favorite status for a product
    fun toggleFavorite(product: Product) {
        Log.d("FavoritesViewModel", "Toggling favorite for: ${product.id} - ${product.name}")

        val currentIds = _savedProductIds.value.toMutableSet()

        if (currentIds.contains(product.id)) {
            // Remove from favorites
            Log.d("FavoritesViewModel", "Removing from favorites")
            currentIds.remove(product.id)
            _savedProducts.value = _savedProducts.value.filter { it.id != product.id }
        } else {
            // Add to favorites
            Log.d("FavoritesViewModel", "Adding to favorites")
            currentIds.add(product.id)
            _savedProducts.value = _savedProducts.value + product
        }

        _savedProductIds.value = currentIds

        // Log the current state after update
        Log.d("FavoritesViewModel", "Current saved IDs: ${_savedProductIds.value}")
        Log.d("FavoritesViewModel", "Current saved products count: ${_savedProducts.value.size}")
    }

    // Check if a product is saved as favorite
    fun isFavorite(productId: String): Boolean {
        return _savedProductIds.value.contains(productId)
    }

    // Remove a product from favorites
    fun removeFromFavorites(productId: String) {
        val currentIds = _savedProductIds.value.toMutableSet()
        currentIds.remove(productId)
        _savedProductIds.value = currentIds

        _savedProducts.value = _savedProducts.value.filter { it.id != productId }
    }
}