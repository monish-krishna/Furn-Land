package com.practise.furn_land.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SuggestionHistory(
    val userId: Int =0,
    val suggestion: String,
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
)
