package com.practise.furn_land.data.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.practise.furn_land.data.entities.*
import com.practise.furn_land.data.entities.relations.CartWithProductAndImages
import com.practise.furn_land.data.entities.relations.OrderListWithProductAndImages
import com.practise.furn_land.data.entities.relations.ProductWithImages
import com.practise.furn_land.data.models.Address

@Dao
interface UserDao {

    //Registration/LogIn
    @Insert
    suspend fun createUser(user: User): Long

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE emailId = :emailId)")
    suspend fun isRegisteredUser(emailId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE emailId = :emailId AND password = :password)")
    suspend fun validatePassword(emailId: String, password: String): Int

    @Query("SELECT id FROM User WHERE emailId = :emailId")
    suspend fun getUserId(emailId: String): Long

    //Favorite
    @Query("SELECT EXISTS(SELECT 1 FROM Favorite WHERE userId = :userId AND productId = :productId)")
    suspend fun isUserFavorite(productId: Int, userId: Int): Int

    @Query("SELECT productId FROM Favorite WHERE userId = :userId")
    suspend fun getUserFavorites(userId: Int): List<Int>

    @Insert
    suspend fun insertFavorite(favorite: Favorite): Long

    @Query("DELETE FROM Favorite WHERE productId = :productId AND userId = :userId")
    suspend fun removeFavorite(userId: Int, productId: Int): Int

    @Query("SELECT * FROM Cart WHERE userId = :userId")
    suspend fun getCartItems(userId: Int): List<CartWithProductAndImages>

    @Insert
    suspend fun insertIntoCart(cart: Cart): Long

    @Query("SELECT EXISTS(SELECT 1 FROM Cart WHERE productId = :productId AND userId = :userId)")
    suspend fun isProductInUserCart(productId: Int, userId: Int): Int

    @Query("UPDATE Cart SET quantity = :quantity WHERE userId = :userId AND productId = :productId")
    suspend fun updateQuantity(userId: Int, productId: Int, quantity: Int)

    @Query("DELETE FROM Cart WHERE productId = :productId AND userId = :userId")
    suspend fun removeCartItem(userId: Int, productId: Int)

    //Orders
    @Insert
    suspend fun addOrder(order: Order): Long

    @Insert
    suspend fun addOrderList(orderList: OrderList)

    @Query("SELECT * FROM `Order` WHERE userId = :userId ORDER BY id DESC")
    suspend fun getOrders(userId: Int): List<Order>

    @Query("SELECT * FROM OrderList WHERE orderId = :orderId")
    suspend fun getOrderList(orderId: Int): List<OrderListWithProductAndImages>

    @Query("DELETE FROM Cart WHERE userId = :userId")
    suspend fun clearCart(userId: Int)

    @Query("SELECT * FROM `Order` WHERE id = :orderId")
    suspend fun getOrder(orderId: Int): Order

    @Query("UPDATE Product SET stockCount = (SELECT stockCount FROM Product WHERE id = :productId)- :quantity WHERE id = :productId")
    suspend fun updateStock(productId: Int, quantity: Int)


    //Profile
    @Query("SELECT name FROM User WHERE id = :userId")
    suspend fun getUserName(userId: Long): String

    @Query("SELECT emailId FROM User WHERE id = :userId")
    suspend fun getUserEmail(userId: Long): String

    @Query("SELECT mobileNumber FROM User WHERE id = :userId")
    suspend fun getUserMobile(userId: Long): String

    @Query("SELECT EXISTS(SELECT 1 FROM User WHERE id = :userId AND pincode IS NOT NULL)")
    suspend fun userHasAddress(userId: Int): Int

    @Query("UPDATE User SET addressLine1 = :addressLine1, addressLine2 = :addressLine2, pincode = :pincode WHERE id = :userId")
    suspend fun addAddress(userId: Int, addressLine1: String, addressLine2: String, pincode: Int)

    @Query("SELECT addressLine1,addressLine2,pincode FROM User WHERE id = :userId")
    suspend fun getUserAddress(userId: Long): Address

    //Suggestions and history
    @Query("SELECT * FROM SuggestionHistory WHERE userId = 0 OR userId = :userId")
    suspend fun getSuggestions(userId: Int): List<SuggestionHistory>

    @RawQuery
    suspend fun getSuggestions(suggestionQuery: SupportSQLiteQuery): List<ProductWithImages>
}