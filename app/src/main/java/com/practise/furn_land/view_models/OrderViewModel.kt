package com.practise.furn_land.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practise.furn_land.data.entities.Cart
import com.practise.furn_land.data.entities.Order
import com.practise.furn_land.data.entities.OrderList
import com.practise.furn_land.data.entities.relations.OrderListWithProductAndImages
import com.practise.furn_land.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val orderStatus = MutableLiveData<Boolean>()
    private val orders = MutableLiveData<List<Order>>()
    private val orderList = MutableLiveData<List<OrderListWithProductAndImages>>()
    private var currentOrderId = 0
    private val order = MutableLiveData<Order>()

    init {
        orderStatus.value = false
    }

    fun getOrderStatus(): LiveData<Boolean> = orderStatus

    fun getOrderId(): Int = currentOrderId

    fun addOrder(userId: Int, cartItems: List<Cart>, totalPrice: Int){
        viewModelScope.launch {
            val orderId = userRepository.addOrder(userId, cartItems, totalPrice.toFloat())
            if (orderId.await() > 0){
                currentOrderId=orderId.await().toInt()
                orderStatus.value = true
            }
        }
    }

    fun cleanStatus() {
        orderStatus.value = false
    }

    fun initOrders(userId: Int) {
        viewModelScope.launch {
            orders.postValue(userRepository.getOrders(userId))
        }
    }

    fun initOrderList(orderId: Int){
        viewModelScope.launch {
            order.postValue(userRepository.getOrder(orderId))
            orderList.postValue(userRepository.getOrderList(orderId))
        }
    }

    fun getOrders(): LiveData<List<Order>> = orders

    fun getOrder(): LiveData<Order> = order

    fun getOrderList(): LiveData<List<OrderListWithProductAndImages>> = orderList
}