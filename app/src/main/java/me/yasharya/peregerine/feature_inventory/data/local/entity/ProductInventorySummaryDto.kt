package me.yasharya.peregerine.feature_inventory.data.local.entity

import androidx.room.Embedded

data class ProductInventorySummaryDto(
    @Embedded
    val product: ProductEntity,
    val totalQtyAvailable: Double,
    val isLowStock: Boolean,
    val isOutOfStock: Boolean
)

