package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderList(
    val orderId : Int,
    val productId : Int,
    val quantity : Int = 1,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
)
