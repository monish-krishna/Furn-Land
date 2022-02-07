package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductImage (
    val productId : Int,
    val productImageIndex: Int,
    val productImageUrl: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)