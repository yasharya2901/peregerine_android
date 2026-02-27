package me.yasharya.peregerine.feature_inventory.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "products",
    indices = [
        Index(value = ["name"]),
        Index(value = ["barcode"], unique = true)
    ]
)
data class ProductEntity (
    @PrimaryKey val id: String,
    val name: String,
    val barcode: String?,
    val sellingPrice: Long,
    val costPrice: Long?,
    val unit: String,
    val stockQty: Double,
    val lowStockThreshold: Double?,
    val notes: String?,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)