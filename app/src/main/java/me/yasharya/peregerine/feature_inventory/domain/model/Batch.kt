package me.yasharya.peregerine.feature_inventory.domain.model

import me.yasharya.peregerine.core.util.Money

data class Batch(
    val id: String,
    val productId: String,
    val purchaseDate: Long,
    val mrp: Money,
    val costPrice: Money,
    val sellingPrice: Money,
    val purchaseQty: Double,
    val qtyOnHand: Double,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
