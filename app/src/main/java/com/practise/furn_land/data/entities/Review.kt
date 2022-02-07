package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Review(
    val productId: Int,
    val userId: Int,
    var rating: Float = 0F,
    var review: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)