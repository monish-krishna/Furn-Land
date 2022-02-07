package com.practise.furn_land.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.furn_land.data.entities.Cart
import com.practise.furn_land.data.entities.relations.CartWithProductAndImages
import com.practise.furn_land.data.repository.UserRepository
import com.practise.furn_land.ui.fragments.CartFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private var _addToCartStatus = MutableLiveData<Int>()
    private var _isProductInUserCart = MutableLiveData<Boolean>()
    private val _cartItems = MutableLiveData<List<CartWithProductAndImages>>()
    private var totalPrice: Int = 0

    init {
        _isProductInUserCart.value = false
    }

    fun initCartItems(userId: Int){
        viewModelScope.launch {
            _cartItems.postValue(userRepository.getCartItems(userId))
        }
    }

    fun insertIntoCart(userId: Int, productId: Int){
        viewModelScope.launch {
            userRepository.insertIntoCart(Cart(userId, productId))
            _addToCartStatus.value = 1
            _isProductInUserCart.value = true
        }
    }

    fun updateQuantity(userId: Int, productId: Int, updatedQuantity: Int){
        viewModelScope.launch {
            userRepository.updateQuantity(userId, productId, updatedQuantity)
            _cartItems.value?.forEach { cartItem ->
                if(cartItem.cart.productId == productId){
                    cartItem.cart.quantity = updatedQuantity
                }
            }
        }
    }

    fun getAddToCartStatus(): LiveData<Int> = _addToCartStatus

    fun cleanUp(){
        _addToCartStatus.value = 0
    }

    fun checkProductInUserCart(productId: Int, userId: Int){
        viewModelScope.launch {
            _isProductInUserCart.value = userRepository.isProductInUserCart(productId, userId)
        }
    }

    fun isProductInUserCart(): MutableLiveData<Boolean> = _isProductInUserCart

    fun getIsProductInUserCart() = _isProductInUserCart.value ?: false

    fun getCartItems(): LiveData<List<CartWithProductAndImages>> = _cartItems

    fun removeCart(userId: Int, productId: Int) {
        viewModelScope.launch {
            userRepository.removeCartItem(userId, productId).also {
                initCartItems(userId)
            }
        }
    }

    suspend fun clearCart(userId: Int) {
        _cartItems.value = emptyList()
        userRepository.clearCart(userId)
    }

    fun setTotalPrice(totalPrice: Int) {
        this.totalPrice = totalPrice
    }

    fun getTotalPrice(): Int {
        return totalPrice
    }
}