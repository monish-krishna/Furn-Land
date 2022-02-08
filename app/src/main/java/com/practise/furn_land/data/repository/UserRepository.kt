package com.practise.furn_land.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.practise.furn_land.data.database.UserDao
import com.practise.furn_land.data.entities.*
import com.practise.furn_land.data.entities.relations.CartWithProductAndImages
import com.practise.furn_land.data.entities.relations.OrderListWithProductAndImages
import com.practise.furn_land.data.entities.relations.ProductWithImages
import com.practise.furn_land.data.models.Address
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.*

class UserRepository(
    private val dao: UserDao
) {
    //User - related
    suspend fun createUser(user : User): Long = withContext(Dispatchers.IO){
        dao.createUser(user)
    }

    suspend fun isRegisteredUser(emailId: String): Int = withContext(Dispatchers.IO){
        dao.isRegisteredUser(emailId)
    }

    suspend fun validatePassword(emailId: String, password: String): Int = withContext(Dispatchers.IO){
        dao.validatePassword(emailId, password)
    }

    suspend fun getUserId(emailId: String): Long = withContext(Dispatchers.IO){
        dao.getUserId(emailId)
    }

    suspend fun userHasAddress(userId: Int): Boolean = withContext(Dispatchers.IO){
        dao.userHasAddress(userId)==1
    }


    // Favorite
    suspend fun isUserFavorite(productId: Int, userId: Int): Int = withContext(Dispatchers.IO){
        dao.isUserFavorite(productId, userId)
    }

    suspend fun getUserFavorites(userId: Int): List<Int> = withContext(Dispatchers.IO){
        dao.getUserFavorites(userId)
    }

    suspend fun insertFavorite(favorite: Favorite): Long = withContext(Dispatchers.IO){
        dao.insertFavorite(favorite)
    }

    suspend fun removeFavorite(userId: Int, productId: Int): Int = withContext(Dispatchers.IO){
        dao.removeFavorite(userId, productId)
    }

    //Cart related
    suspend fun insertIntoCart(cart: Cart): Long = withContext(Dispatchers.IO){
        dao.insertIntoCart(cart)
    }

    suspend fun isProductInUserCart(productId: Int, userId: Int): Boolean = withContext(Dispatchers.IO){
        dao.isProductInUserCart(productId, userId)==1
    }

    suspend fun getCartItems(userId: Int): List<CartWithProductAndImages> = withContext(Dispatchers.IO){
        dao.getCartItems(userId)
    }

    suspend fun updateQuantity(userId: Int, productId: Int, quantity: Int) = withContext(Dispatchers.IO){
        dao.updateQuantity(userId,productId,quantity)
    }


    suspend fun removeCartItem(userId: Int, productId: Int) = withContext(Dispatchers.IO){
        dao.removeCartItem(userId, productId)
    }

    suspend fun addOrder(userId: Int, cartItems: List<Cart>,totalPrice: Float) = withContext(Dispatchers.IO){
        val calendar = Calendar.getInstance()
        val dateFormat = DateFormat.getDateTimeInstance()
        val dateTime = dateFormat.format(calendar.time)
        val orderId = async {dao.addOrder(Order(userId,cartItems.size,dateTime,totalPrice))  }
        val orderListInsertion = async{
            with(orderId.await()) {
                cartItems.forEach { cartItem ->
                    dao.addOrderList(OrderList(this.toInt(), cartItem.productId, cartItem.quantity))
                    dao.updateStock(cartItem.productId, cartItem.quantity)
                }
            }
        }
        orderListInsertion.await()
        return@withContext orderId
    }

    suspend fun getOrders(userId: Int): List<Order> = withContext(Dispatchers.IO){
        dao.getOrders(userId)
    }

    suspend fun getOrderList(orderId: Int): List<OrderListWithProductAndImages> = withContext(Dispatchers.IO){
        dao.getOrderList(orderId)
    }

    suspend fun clearCart(userId: Int) = withContext(Dispatchers.IO){
        dao.clearCart(userId)
    }

    suspend fun getOrder(orderId: Int): Order = withContext(Dispatchers.IO) {
        dao.getOrder(orderId)
    }

    suspend fun getUsername(userId: Long): String = withContext(Dispatchers.IO){
        dao.getUserName(userId)
    }

    suspend fun getUserEmail(userId: Long): String = withContext(Dispatchers.IO){
        dao.getUserEmail(userId)
    }

    suspend fun getUserMobile(userId: Long): String = withContext(Dispatchers.IO){
        dao.getUserMobile(userId)
    }

    suspend fun addAddress(userId: Int, address: Address) = withContext(Dispatchers.IO) {
         dao.addAddress(userId,address.addressLine1,address.addressLine2,address.pincode)
    }

    suspend fun getUserAddress(userId: Long): Address = withContext(Dispatchers.IO){
        dao.getUserAddress(userId)
    }

    //Suggestions
    suspend fun insertSuggestion(suggestionHistory: SuggestionHistory) = withContext(Dispatchers.IO){
        dao.insertSuggestion(suggestionHistory)
    }

    suspend fun getSuggestions(userId: Int): List<SuggestionHistory> = withContext(Dispatchers.IO){
        dao.getSuggestions(userId)
    }

    suspend fun getSuggestions(searchQuery: List<String>, userId: Int): List<SuggestionHistory> = withContext(Dispatchers.IO){
        dao.getSuggestions(generateSuggestionQuery(searchQuery, userId))
    }

    private fun generateSuggestionQuery(query: List<String>, userId: Int): SupportSQLiteQuery {
        var queryText = "SELECT * FROM SuggestionHistory WHERE (suggestion LIKE \'${query[0]}\'"
        val args = arrayListOf<String>()
        query.drop(1).forEach { searchStringPart ->
            args.add(searchStringPart)
            queryText += " OR suggestion LIKE ?"
        }
        queryText += ") AND (userId = 0 OR userId = $userId) ORDER BY id DESC"
        return SimpleSQLiteQuery(queryText, args.toArray())
    }

    suspend fun removeSuggestionHistory(suggestionHistory: SuggestionHistory) {
        dao.removeSuggestionHistory(suggestionHistory)
    }

    suspend fun isSuggestionPresent(suggestion: String): Boolean= withContext(Dispatchers.IO) {
        dao.isSuggestionPresent(suggestion)==1
    }
}