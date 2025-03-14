package com.example.arfurnitureapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class Address(
    val fullName: String,
    val streetAddress: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val isDefault: Boolean = false
)

data class PaymentMethod(
    val cardNumber: String,
    val cardHolderName: String,
    val expiryDate: String,
    val cvv: String,
    val isDefault: Boolean = false
)

data class OrderSummary(
    val orderId: String,
    val items: List<CartItem>,
    val subtotal: Double,
    val tax: Double,
    val shipping: Double,
    val total: Double,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod,
    val orderDate: String,
    val orderStatus: String = "Processing"
)

class CheckoutViewModel : ViewModel() {
    // Addresses
    private val _addresses = MutableStateFlow<List<Address>>(emptyList())
    val addresses: StateFlow<List<Address>> = _addresses.asStateFlow()

    // Selected address
    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()

    // Payment methods
    private val _paymentMethods = MutableStateFlow<List<PaymentMethod>>(emptyList())
    val paymentMethods: StateFlow<List<PaymentMethod>> = _paymentMethods.asStateFlow()

    // Selected payment method
    private val _selectedPaymentMethod = MutableStateFlow<PaymentMethod?>(null)
    val selectedPaymentMethod: StateFlow<PaymentMethod?> = _selectedPaymentMethod.asStateFlow()

    // Checkout steps
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    // Order summary
    private val _orderSummary = MutableStateFlow<OrderSummary?>(null)
    val orderSummary: StateFlow<OrderSummary?> = _orderSummary.asStateFlow()

    // Error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Sample data for demo
    init {
        // Add sample address
        val sampleAddress = Address(
            fullName = "John Doe",
            streetAddress = "123 Main St",
            city = "Anytown",
            state = "CA",
            zipCode = "12345",
            country = "United States",
            isDefault = true
        )
        _addresses.value = listOf(sampleAddress)
        _selectedAddress.value = sampleAddress

        // Add sample payment method
        val samplePayment = PaymentMethod(
            cardNumber = "•••• •••• •••• 1234",
            cardHolderName = "John Doe",
            expiryDate = "12/25",
            cvv = "***",
            isDefault = true
        )
        _paymentMethods.value = listOf(samplePayment)
        _selectedPaymentMethod.value = samplePayment
    }

    // Add a new address
    fun addAddress(address: Address) {
        // If first address, set as default
        val updatedAddress = if (_addresses.value.isEmpty()) {
            address.copy(isDefault = true)
        } else {
            address
        }

        _addresses.value = _addresses.value + updatedAddress

        // If no address is selected or this is marked as default, select it
        if (_selectedAddress.value == null || updatedAddress.isDefault) {
            _selectedAddress.value = updatedAddress
        }
    }

    // Select an address
    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    // Add a new payment method
    fun addPaymentMethod(paymentMethod: PaymentMethod) {
        // If first payment method, set as default
        val updatedPayment = if (_paymentMethods.value.isEmpty()) {
            paymentMethod.copy(isDefault = true)
        } else {
            paymentMethod
        }

        _paymentMethods.value = _paymentMethods.value + updatedPayment

        // If no payment method is selected or this is marked as default, select it
        if (_selectedPaymentMethod.value == null || updatedPayment.isDefault) {
            _selectedPaymentMethod.value = updatedPayment
        }
    }

    // Select a payment method
    fun selectPaymentMethod(paymentMethod: PaymentMethod) {
        _selectedPaymentMethod.value = paymentMethod
    }

    // Go to next checkout step
    fun nextStep() {
        _currentStep.value++
    }

    // Go to previous checkout step
    fun previousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value--
        }
    }

    // Process order
    fun placeOrder(items: List<CartItem>, subtotal: Double): Boolean {
        val selectedAddress = _selectedAddress.value
        val selectedPayment = _selectedPaymentMethod.value

        if (selectedAddress == null) {
            _errorMessage.value = "Please select a shipping address"
            return false
        }

        if (selectedPayment == null) {
            _errorMessage.value = "Please select a payment method"
            return false
        }

        // Calculate totals
        val tax = subtotal * 0.08 // 8% tax
        val shipping = if (subtotal > 50) 0.0 else 9.99 // Free shipping over $50
        val total = subtotal + tax + shipping

        // Create order summary
        val order = OrderSummary(
            orderId = UUID.randomUUID().toString().substring(0, 8),
            items = items,
            subtotal = subtotal,
            tax = tax,
            shipping = shipping,
            total = total,
            shippingAddress = selectedAddress,
            paymentMethod = selectedPayment,
            orderDate = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
        )

        _orderSummary.value = order
        return true
    }

    // Reset checkout
    fun resetCheckout() {
        _currentStep.value = 1
        _orderSummary.value = null
        _errorMessage.value = null
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
}