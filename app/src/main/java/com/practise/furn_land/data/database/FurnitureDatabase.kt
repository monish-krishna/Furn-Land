package com.practise.furn_land.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practise.furn_land.data.entities.*

@Database(
    entities = [
        Cart::class,
        Category::class,
        Favorite::class,
        Order::class,
        OrderList::class,
        Product::class,
        ProductDetail::class,
        ProductImage::class,
        Review::class,
        User::class,
        SuggestionHistory::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FurnitureDatabase : RoomDatabase() {

    abstract val furnitureDao : FurnitureDao

    abstract val userDao : UserDao
}