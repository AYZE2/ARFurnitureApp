package com.example.arfurnitureapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arfurnitureapp.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)

class CartViewModel : ViewModel() {
    // Cart items
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Total price
    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    // Add product to cart
    fun addToCart(product: Product) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == product.id }

        if (existingItem != null) {
            // Update quantity if item already in cart
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            // Add new item to cart
            currentItems.add(CartItem(product))
        }

        _cartItems.value = currentItems
        updateTotalPrice()
    }

    // Remove item from cart
    fun removeFromCart(productId: String) {
        _cartItems.value = _cartItems.value.filter { it.product.id != productId }
        updateTotalPrice()
    }

    // Update item quantity
    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }

        _cartItems.value = _cartItems.value.map {
            if (it.product.id == productId) {
                it.copy(quantity = quantity)
            } else {
                it
            }
        }
        updateTotalPrice()
    }

    // Clear cart
    fun clearCart() {
        _cartItems.value = emptyList()
        updateTotalPrice()
    }

    // Get cart size
    fun getCartSize(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }

    // Calculate and update total price
    private fun updateTotalPrice() {
        _totalPrice.value = _cartItems.value.sumOf {
            it.product.price * it.quantity
        }
    }
}