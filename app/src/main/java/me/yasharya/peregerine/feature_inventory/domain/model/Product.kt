package me.yasharya.peregerine.feature_inventory.domain.model

import me.yasharya.peregerine.core.util.Money

data class Product (
    val id: String,
    val name: String,
    val barcode: String?,
    val sellingPrice: Money,
    val costPrice: Money?,
    val unit: String,              // "pcs", "kg", "litre"
    val stockQty: Double,
    val lowStockThreshold: Double?,
    val notes: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)