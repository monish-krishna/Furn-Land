package com.practise.furn_land.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practise.furn_land.data.models.Address

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    var name : String,
    var emailId : String,
    var password : String,
    var mobileNumber : String,
    @Embedded
    var address :Address?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}