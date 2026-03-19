package me.yasharya.peregerine.feature_inventory.data.local.entity

data class StockLedgerWithProductDto(
    val id: String,
    val productId: String,
    val batchId: String?,
    val type: String,
    val deltaQty: Double,
    val referenceId: String?,
    val note: String?,
    val createdAt: Long,
    val productName: String,
    val productUnit: String
)