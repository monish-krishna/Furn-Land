package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Order(
    val userId : Int,
    val productCount: Int,
    val orderDateTime : String,
    val totalPrice : Float,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
)
