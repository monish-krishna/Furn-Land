package com.practise.furn_land.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.practise.furn_land.data.entities.OrderList
import com.practise.furn_land.data.entities.Product

data class OrderListWithProductAndImages(
    @Embedded
    val orderList: OrderList,
    @Relation(
        entity = Product::class,
        parentColumn = "productId",
        entityColumn = "id"
    )
    val productWithImages: ProductWithImages
)
