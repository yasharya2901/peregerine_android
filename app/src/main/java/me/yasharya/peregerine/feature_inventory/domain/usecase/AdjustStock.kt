package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class AdjustStock(private val repo: InventoryRepository) {
    suspend operator fun invoke(
        productId: String,
        deltaQty: Double,
        type: StockChangeType,
        note: String?,
        referenceId: String? = null
    ) = repo.adjustStock(productId, deltaQty, type, note, referenceId)
}
