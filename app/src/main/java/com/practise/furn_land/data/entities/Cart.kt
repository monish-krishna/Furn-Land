package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart(
    val userId: Int,
    val productId: Int,
    var quantity: Int = 1,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
)
