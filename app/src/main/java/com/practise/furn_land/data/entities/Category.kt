package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey
    val id : Int,
    val name : String,
    val group : String,
    val image : String
)
