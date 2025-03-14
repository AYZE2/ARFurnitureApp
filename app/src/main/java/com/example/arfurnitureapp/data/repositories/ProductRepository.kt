package com.example.arfurnitureapp.data.repositories

import com.example.arfurnitureapp.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ProductRepository(private val firestore: FirebaseFirestore) {
    fun getProducts(): Flow<List<Product>> = flow {
        val snapshot = firestore.collection("products").get().await()
        val products = snapshot.documents.map { doc ->
            Product(
                id = doc.id,
                name = doc.getString("name") ?: "",
                description = doc.getString("description") ?: "",
                price = doc.getDouble("price") ?: 0.0,
                category = doc.getString("category") ?: "",
                categoryId = doc.getString("categoryId") ?: "",
                imageResId = doc.getLong("imageResId")?.toInt() ?: 0,
                modelResId = doc.getLong("modelResId")?.toInt()
            )
        }
        emit(products)
    }

    fun getProductsByCategory(categoryId: String): Flow<List<Product>> = flow {
        val snapshot = firestore.collection("products")
            .whereEqualTo("categoryId", categoryId)
            .get().await()

        val products = snapshot.documents.map { doc ->
            Product(
                id = doc.id,
                name = doc.getString("name") ?: "",
                description = doc.getString("description") ?: "",
                price = doc.getDouble("price") ?: 0.0,
                category = doc.getString("category") ?: "",
                categoryId = doc.getString("categoryId") ?: "",
                imageResId = doc.getLong("imageResId")?.toInt() ?: 0,
                modelResId = doc.getLong("modelResId")?.toInt()
            )
        }
        emit(products)
    }

    // Function to populate initial data
    suspend fun populateProductsIfEmpty() {
        val snapshot = firestore.collection("products").get().await()
        if (snapshot.isEmpty) {
            // Use your existing SampleData to populate Firestore
            SampleData.getPopularProducts().forEach { product ->
                firestore.collection("products").document(product.id)
                    .set(mapOf(
                        "name" to product.name,
                        "description" to product.description,
                        "price" to product.price,
                        "category" to product.category,
                        "categoryId" to product.categoryId,
                        "imageResId" to product.imageResId,
                        "modelResId" to product.modelResId
                    ))
            }
        }
    }
}