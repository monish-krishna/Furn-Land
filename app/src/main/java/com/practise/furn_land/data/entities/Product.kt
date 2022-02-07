package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey
    val id : Int,
    val name : String,
    val brandName : String,
    val originalPrice : Float,
    var currentPrice : Float,
    var totalRating : Float?,
    var stockCount : Int = 0,
    val categoryId : Int
)
