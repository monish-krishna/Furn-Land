package com.practise.furn_land.data.entities.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.practise.furn_land.data.entities.Category

data class CategoryWithProducts (
    @Embedded
    val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val productsWithImages: List<ProductWithImages>
)