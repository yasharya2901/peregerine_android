package me.yasharya.peregerine.feature_inventory.domain.model

import me.yasharya.peregerine.core.util.Money

data class Product (
    val id: String,
    val name: String,
    val barcode: String?,
    val unit: String,              // "pcs", "kg", "litre"
    val defaultSellingPrice: Money?,
    val defaultCostPrice: Money?,
    val defaultMRP: Money?,
    val lowStockThreshold: Double?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)