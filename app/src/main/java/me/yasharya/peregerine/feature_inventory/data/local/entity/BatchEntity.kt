package me.yasharya.peregerine.feature_inventory.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "batch",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )],
    indices = [
        Index(value = ["productId"]),
        Index(value = ["purchaseDate"]),
        Index(value = ["isActive"])
    ]
)
data class BatchEntity (
    @PrimaryKey val id: String,
    val productId: String,
    val purchaseDate: Long,
    val mrp: Long,
    val costPrice: Long,
    val sellingPrice: Long,
    val qtyOnHand: Double,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)