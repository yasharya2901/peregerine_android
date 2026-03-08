package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class AdjustStock(private val repository: InventoryRepository) {
    suspend operator fun invoke(productId: String, batchId: String, deltaQty: Double, type: StockChangeType, referenceId: String?, note: String?) = repository.adjustStockTransactional(productId, batchId, deltaQty, type, referenceId, note)
}
