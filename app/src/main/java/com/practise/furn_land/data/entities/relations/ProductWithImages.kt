package com.practise.furn_land.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.practise.furn_land.data.entities.Product
import com.practise.furn_land.data.entities.ProductImage

data class ProductWithImages(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "id",
        entityColumn = "productId"
    )
    val productImages: List<ProductImage>
)
