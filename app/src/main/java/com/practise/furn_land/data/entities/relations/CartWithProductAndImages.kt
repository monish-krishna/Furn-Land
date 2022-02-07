package com.practise.furn_land.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.practise.furn_land.data.entities.Cart
import com.practise.furn_land.data.entities.Product

data class CartWithProductAndImages(
    @Embedded val cart: Cart,
    @Relation(
        entity = Product::class,
        parentColumn = "productId",
        entityColumn = "id"
    )
    val productWithImages: ProductWithImages
)
