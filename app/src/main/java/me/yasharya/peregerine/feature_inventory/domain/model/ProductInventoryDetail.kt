package me.yasharya.peregerine.feature_inventory.domain.model

data class ProductInventoryDetail (
    val product: Product,
    val totalQtyAvailable: Double,
    val isLowStock: Boolean,
    val isOutOfStock: Boolean,
    val batches: List<Batch>
)