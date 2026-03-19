package me.yasharya.peregerine.feature_inventory.domain.model

data class StockLedgerEntryWithProduct(
    val id: String,
    val productId: String,
    val productName: String,
    val productUnit: String,
    val batchId: String?,
    val type: StockChangeType,
    val deltaQty: Double,
    val referenceId: String?,
    val note: String?,
    val createdAt: Long
)
