package com.practise.furn_land.data.database

import android.util.Log
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.practise.furn_land.data.entities.Category
import com.practise.furn_land.data.entities.Product
import com.practise.furn_land.data.entities.ProductDetail
import com.practise.furn_land.data.entities.ProductImage
import com.practise.furn_land.data.entities.relations.CategoryWithProducts
import com.practise.furn_land.data.entities.relations.ProductWithImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
interface FurnitureDao {
    //Products
    @Query("SELECT * FROM Product WHERE categoryId = :categoryId")
    suspend fun getProducts(categoryId : Int): List<Product>

    @Query("SELECT * FROM Product")
    suspend fun getProducts(): List<Product>

    @Query("SELECT * FROM Product WHERE categoryId IN (:categoryIds)")
    suspend fun getProducts(categoryIds: List<Int>): List<Product>

    @Query("SELECT * FROM Product WHERE id = :productId")
    suspend fun getProduct(productId : Int): Product

    @Query("SELECT * FROM ProductImage WHERE productId = :productId ORDER BY productImageIndex")
    suspend fun getProductImages(productId: Int): List<ProductImage>

    @Query("SELECT * FROM ProductDetail WHERE productId = :productId ORDER BY id")
    suspend fun getProductDetails(productId: Int): List<ProductDetail>

    @Query("SELECT * FROM Category")
    suspend fun getCategories(): List<Category>

    @Query("SELECT * FROM Product WHERE categoryId = :categoryId")
    suspend fun getProductWithImages(categoryId : Int): List<ProductWithImages>

    @Query("SELECT * FROM Product WHERE categoryId IN (:categoryIds)")
    suspend fun getProductWithImages(categoryIds: List<Int>): List<ProductWithImages>

    @Query("SELECT * FROM Product")
    suspend fun getProductWithImages(): List<ProductWithImages>

    @Query("SELECT * FROM Product WHERE id IN (:productIds)")
    suspend fun getProductWithImagesByProductIds(productIds: List<Int>): List<ProductWithImages>

    @Query("SELECT * FROM Product WHERE name IN (:searchQueries) or brandName IN (:searchQueries)")
    suspend fun getProductWithImagesWithQuery(searchQueries: List<String>): List<ProductWithImages>

    @RawQuery
    suspend fun getProductWithImagesWithQuery(searchQueries: SupportSQLiteQuery): List<ProductWithImages>
}