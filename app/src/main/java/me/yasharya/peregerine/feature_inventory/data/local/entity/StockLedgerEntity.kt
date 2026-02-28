package me.yasharya.peregerine.feature_inventory.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType


@Entity(
    tableName = "stock_ledger",
    indices = [
        Index(value = ["productId"]),
        Index(value = ["createdAt"])
    ]
)
data class StockLedgerEntity (
    @PrimaryKey val id: String,
    val productId: String,
    val type: StockChangeType,
    val deltaQty: Double,
    val referenceId: String?,
    val note: String?,
    val createdAt: Long
)
