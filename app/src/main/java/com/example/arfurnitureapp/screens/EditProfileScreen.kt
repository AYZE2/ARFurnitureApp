package com.example.arfurnitureapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.arfurnitureapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()
    val updateProfileSuccess by authViewModel.updateProfileSuccess.collectAsState()

    // If not logged in, redirect to login
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("editProfile") { inclusive = true }
            }
        }
        return
    }

    var name by remember { mutableStateOf(currentUser?.name ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var address by remember { mutableStateOf(currentUser?.address ?: "") }

    // Show success/failure message
    LaunchedEffect(updateProfileSuccess) {
        if (updateProfileSuccess == true) {
            // Show success message
            // Reset the success message after a short time
            authViewModel.clearUpdateProfileSuccess()
        } else if (updateProfileSuccess == false) {
            // Show failure message
            authViewModel.clearUpdateProfileSuccess()  // Optionally reset after showing failure
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success/Failure message
            updateProfileSuccess?.let {
                if (it) {
                    Text("Profile updated successfully!", color = MaterialTheme.colorScheme.primary)
                } else {
                    Text("Failed to update profile.", color = MaterialTheme.colorScheme.error)
                }
            }

            // Profile information form...
            // Your existing form goes here

            // Save button
            Button(
                onClick = {
                    authViewModel.updateUserProfile(name, phone, address)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("SAVE CHANGES")
            }
        }
    }
}

