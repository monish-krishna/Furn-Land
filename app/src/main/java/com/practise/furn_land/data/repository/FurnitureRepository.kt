package com.practise.furn_land.data.repository

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.practise.furn_land.data.database.FurnitureDao
import com.practise.furn_land.data.entities.Category
import com.practise.furn_land.data.entities.Product
import com.practise.furn_land.data.entities.ProductDetail
import com.practise.furn_land.data.entities.ProductImage
import com.practise.furn_land.data.entities.relations.ProductWithImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FurnitureRepository(
    private val dao : FurnitureDao
) {

    suspend fun getProducts(categoryId : Int): List<Product> = withContext(Dispatchers.IO){
        dao.getProducts(categoryId)
    }

    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO){
        dao.getProducts()
    }

    suspend fun getProduct(productId: Int): Product = withContext(Dispatchers.IO){
        dao.getProduct(productId)
    }

    suspend fun getProductImages(productId: Int): List<ProductImage> = withContext(Dispatchers.IO){
        dao.getProductImages(productId)
    }

    suspend fun getProductDetails(productId: Int): List<ProductDetail> = withContext(Dispatchers.IO) {
        dao.getProductDetails(productId)
    }

    suspend fun getProducts(categoryIds: List<Int>): List<Product> = withContext(Dispatchers.IO) {
        dao.getProducts(categoryIds)
    }

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO){
        dao.getCategories()
    }

    suspend fun getProductWithImages(categoryId : Int): List<ProductWithImages> = withContext(Dispatchers.IO){
        dao.getProductWithImages(categoryId)
    }

    suspend fun getProductWithImages(categoryIds: List<Int>): List<ProductWithImages> = withContext(Dispatchers.IO){
        dao.getProductWithImages(categoryIds)
    }

    suspend fun getProductWithImages(): List<ProductWithImages> = withContext(Dispatchers.IO){
        dao.getProductWithImages()
    }

    suspend fun getProductWithImagesByProductIds(productIds: List<Int>): List<ProductWithImages> = withContext(Dispatchers.IO){
        dao.getProductWithImagesByProductIds(productIds)
    }

    suspend fun getProductWithImagesWithQuery(searchQuery: List<String>): List<ProductWithImages> = withContext(Dispatchers.IO){
        dao.getProductWithImagesWithQuery(generateSearchQuery(searchQuery))
    }

    private fun generateSearchQuery(query: List<String>): SupportSQLiteQuery{
        var queryText = "SELECT * FROM Product WHERE name LIKE \'${query[0]}\' or brandName LIKE \'${query[0]}\'"
        val args = arrayListOf<String>()
        query.drop(1).forEach { searchStringPart ->
            args.add(searchStringPart)
            args.add(searchStringPart)
            queryText += " OR name LIKE ? or brandName LIKE ? "
        }
        Log.i("Search","args - $args")
        Log.i("Search","args - $queryText")
        val simpleSQLiteQuery = SimpleSQLiteQuery(queryText,args.toArray())
        Log.i("Search","query as SimpleSQLiteQuery - ${simpleSQLiteQuery.sql}")
        return simpleSQLiteQuery
    }
}