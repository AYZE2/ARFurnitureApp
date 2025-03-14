package com.example.arfurnitureapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arfurnitureapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _signupError = MutableStateFlow<String?>(null)
    val signupError: StateFlow<String?> = _signupError.asStateFlow()

    private val _updateProfileSuccess = MutableStateFlow<Boolean?>(null)
    val updateProfileSuccess: StateFlow<Boolean?> = _updateProfileSuccess.asStateFlow()

    init {
        auth.currentUser?.let { fetchUserData(it.uid) }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _isLoggedIn.value = true
                fetchUserData(it.user?.uid ?: "")
            }
            .addOnFailureListener {
                _loginError.value = "Login failed: ${it.localizedMessage}"
            }
    }

    fun signup(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener
                val newUser = User(id = userId, email = email, name = name)

                // Save user data in Firestore
                db.collection("users").document(userId).set(newUser)
                    .addOnSuccessListener {
                        _isLoggedIn.value = true
                        _currentUser.value = newUser
                    }
                    .addOnFailureListener {
                        _signupError.value = "Failed to save user: ${it.localizedMessage}"
                    }
            }
            .addOnFailureListener {
                _signupError.value = "Signup failed: ${it.localizedMessage}"
            }
    }

    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
        _currentUser.value = null
    }

    private fun fetchUserData(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let { _currentUser.value = it }
            }
            .addOnFailureListener {
                _loginError.value = "Failed to fetch user data"
            }
    }

    fun updateUserProfile(name: String, phone: String, address: String) {
        val currentUser = _currentUser.value ?: return

        val updatedUser = currentUser.copy(name = name, phone = phone, address = address)

        db.collection("users").document(currentUser.id)
            .set(updatedUser)
            .addOnSuccessListener {
                _currentUser.value = updatedUser
                _updateProfileSuccess.value = true  // Notify UI of success
            }
            .addOnFailureListener {
                _updateProfileSuccess.value = false  // Notify UI of failure
                _signupError.value = "Failed to update profile"
            }
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    fun clearSignupError() {
        _signupError.value = null
    }

    fun clearUpdateProfileSuccess() {
        _updateProfileSuccess.value = null
    }
}
