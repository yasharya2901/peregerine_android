package me.yasharya.peregerine.feature_inventory.domain.model

data class StockLedgerEntry(
    val id: String,
    val productId: String,
    val batchId: String?,
    val type: StockChangeType,
    val deltaQty: Double,
    val referenceId: String?, // invoiceId later
    val note: String?,
    val createdAt: Long
)