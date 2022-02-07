package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ProductDetail(
    val productId : Int,
    val productDetailTitle : String,
    val productDetailContent : String,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
)
