package com.example.arfurnitureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.arfurnitureapp.screens.ARViewScreen
import com.example.arfurnitureapp.screens.CartScreen
import com.example.arfurnitureapp.screens.CatergoriesScreen
import com.example.arfurnitureapp.screens.CheckoutScreen
import com.example.arfurnitureapp.screens.EditProfileScreen
import com.example.arfurnitureapp.screens.HomeScreen
import com.example.arfurnitureapp.screens.LoginScreen
import com.example.arfurnitureapp.screens.ProductDetailScreen
import com.example.arfurnitureapp.screens.ProductListScreen
import com.example.arfurnitureapp.screens.ProfileScreen
import com.example.arfurnitureapp.screens.SavedItemsScreen
import com.example.arfurnitureapp.screens.SearchScreen
import com.example.arfurnitureapp.screens.SignupScreen
import com.example.arfurnitureapp.ui.theme.ARFurnitureAppTheme
import com.example.arfurnitureapp.viewmodel.AuthViewModel
import com.example.arfurnitureapp.viewmodel.CartViewModel
import com.example.arfurnitureapp.viewmodel.FavoritesViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            ARFurnitureAppTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    IKEARCloneApp()
                }
            }
        }
    }
}

@Composable
fun IKEARCloneApp() {
    val navController = rememberNavController()
    val favoritesViewModel: FavoritesViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("categories") {
            CatergoriesScreen(navController = navController)
        }
        composable("productList/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")
            ProductListScreen(categoryId = categoryId ?: "", navController = navController)
        }
        composable("productDetail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ProductDetailScreen(productId = productId ?: "",
                navController = navController,
                favoritesViewModel = favoritesViewModel,
                cartViewModel = cartViewModel)
        }
        composable("arView/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            ARViewScreen(productId = productId ?: "")
        }
        composable("saved") {
            SavedItemsScreen(
                navController = navController,
                favoritesViewModel = favoritesViewModel
            )
        }

        composable("search") {
            SearchScreen(navController = navController)
        }
        composable("cart") {
            CartScreen(
                navController = navController,
                cartViewModel = cartViewModel
            )
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("signup") {
            SignupScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel,
                favoritesViewModel = favoritesViewModel,
                cartViewModel = cartViewModel
            )
        }
        composable("editProfile") {
            EditProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }
        composable("checkout") {
            CheckoutScreen(
                navController = navController,
                cartViewModel = cartViewModel,
                checkoutViewModel = viewModel(),
                authViewModel = authViewModel
            )
        }
    }
}
