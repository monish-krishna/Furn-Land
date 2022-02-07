package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorite(
    val userId : Int,
    val productId : Int,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
)
